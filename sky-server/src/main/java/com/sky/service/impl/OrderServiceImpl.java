package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.OrderService;
import com.sky.utils.HttpClientUtil;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.webscoket.WebSocketServer;
import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.sky.entity.Orders.PENDING_PAYMENT;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Value("${sky.shop.AK}")
    public String AK;
    public String URL_GEO = "https://api.map.baidu.com/geocoding/v3?";
    public String URL_DIR = "https://api.map.baidu.com/directionlite/v1/driving?";
    @Value("${sky.shop.address}")
    public String LOCAL_ADDRESS;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private WebSocketServer webSocketServer;

    /**
     * 提交订单
     *
     * @param ordersSubmitDTO
     * @return
     */
    @Override
    @Transactional
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) throws Exception {
        //1、校验地址是否为空
        AddressBook addressBook = addressBookMapper.selectById(ordersSubmitDTO.getAddressBookId());
        if (Objects.isNull(addressBook)) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        //2、校验配送范围
        checkOutOfRange(addressBook.getCityName() + addressBook.getDistrictName() + addressBook.getDetail());
        //3、校验购物车是否为空
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if (Collections.isEmpty(list)) {
            throw new AddressBookBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        //4、保存订单
        //构造订单数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        //根据地址簿信息添加订单信息
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setAddress(addressBook.getDetail());
        //生成订单id
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setStatus(PENDING_PAYMENT);
        orders.setUserId(userId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        //向订单表插入数据
        orderMapper.insert(orders);
        //5、批量保存订单详细
        //向订单详细表插入数据
        ArrayList<OrderDetail> orderDetails = new ArrayList<>();
        list.forEach(shop -> {
            OrderDetail detail = new OrderDetail();
            BeanUtils.copyProperties(shop, detail);
            detail.setOrderId(orders.getId());
            orderDetails.add(detail);
        });
        //向明细表插入n条数据
        orderDetailMapper.insertList(orderDetails);
        //6、清空购物车信息
        shoppingCartMapper.clean(userId);
        //7、构造数据返回前端
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId()).orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime())
                .build();
        return orderSubmitVO;
    }

    /**
     * 订单分页搜索
     *
     * @return
     */
    @Override
    public PageResult historyOrders(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        //分页查询订单
        Page<Orders> p = (Page<Orders>) orderMapper.selectHistoryOrders(BaseContext.getCurrentId(), ordersPageQueryDTO.getStatus());
        //将每个订单的详细插入订单中
        List<OrderVO> list = new ArrayList<>();
        for (Orders orders : p) {
            // 查询订单明细
            List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orders.getId());
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(orders, orderVO);
            orderVO.setOrderDetailList(orderDetails);
            list.add(orderVO);
        }
        //返回结果
        return new PageResult(p.getTotal(), list);
    }

    /**
     * 订单详情
     *
     * @param id
     * @return
     */
    @Override
    public OrderVO orderDetail(Long id) {
        //1、查询订单信息
        Orders order = orderMapper.selectByOrderId(id);
        OrderVO orderVO = new OrderVO();
        //2、将订单信息复制到orderVo
        BeanUtils.copyProperties(order, orderVO);
        //3、将订单的详细信息查询并插入到orderVO中
        List<OrderDetail> byOrderId = orderDetailMapper.getByOrderId(id);
        orderVO.setOrderDetailList(byOrderId);
        //4、返回结果
        return orderVO;
    }

    /**
     * 取消订单
     *
     * @return
     */
    @Override
    public void cancel(Long id) {
        //- 商家已接单状态下，用户取消订单需电话沟通商家
        //- 派送中状态下，用户取消订单需电话沟通商家
        //- 如果在待接单状态下取消订单，需要给用户退款
        Orders order = orderMapper.selectByOrderId(id);
        if (order == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        Integer status = order.getStatus();
        if (status > 2) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        if (status == Orders.PENDING_PAYMENT || status == Orders.TO_BE_CONFIRMED) {
            Orders orders = Orders.builder()
                    .status(Orders.CANCELLED)
                    .id(id)
                    .cancelTime(LocalDateTime.now())
                    .cancelReason("用户取消")
                    .build();
            orderMapper.updateById(orders);
            //并退款
        } else {
            //通知商家
        }
    }

    /**
     * 再来一单
     *
     * @param id
     */
    @Override
    @Transactional
    public void repetition(Long id) {
        // 查询当前用户id
        Long userId = BaseContext.getCurrentId();

        // 根据订单id查询当前订单详情
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);

        // 将订单详情对象转换为购物车对象
        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map(x -> {
            ShoppingCart shoppingCart = new ShoppingCart();

            // 将原订单详情里面的菜品信息重新复制到购物车对象中
            BeanUtils.copyProperties(x, shoppingCart);
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());

            return shoppingCart;
        }).collect(Collectors.toList());

        // 将购物车对象批量添加到数据库
        shoppingCartMapper.insertBatch(shoppingCartList);
    }

    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO queryDTO) {
        PageHelper.startPage(queryDTO.getPage(), queryDTO.getPageSize());
        // 查询订单信息
        List<OrderVO> orderVOS = orderMapper.selectSearch(queryDTO);
        orderVOS.forEach(orderVO -> {
            // 查询订单明细
            List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orderVO.getId());
            // 拼接订单菜品名称
            orderVO.setOrderDishes(getOrderDishesStr(orderDetails));
            orderVO.setOrderDetailList(orderDetails);
        });
        Page<OrderVO> p = (Page<OrderVO>) orderVOS;
        return new PageResult(p.getTotal(), p.getResult());
    }

    private String getOrderDishesStr(List<OrderDetail> orderDetails) {
        StringBuilder sb = new StringBuilder();
        orderDetails.forEach(orderDetail -> sb.append(orderDetail.getName() + "*" + orderDetail.getNumber() + ";"));
        return sb.toString();
    }

    /**
     * 订单详情
     *
     * @param id
     * @return
     */
    @Override
    public OrderVO details(Long id) {
        Orders orders = orderMapper.selectByOrderId(id);
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetailMapper.getByOrderId(id));
        return orderVO;
    }

    /**
     * 管理端取消订单
     *
     * @param ordersCancelDTO
     */
    @Override
    public void cancelOrder(OrdersCancelDTO ordersCancelDTO) {
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersCancelDTO, orders);
        orders.setCancelTime(LocalDateTime.now());
        orders.setStatus(Orders.CANCELLED);
        orderMapper.updateById(orders);
    }

    /**
     * 查询各个状态的数量
     */
    @Override
    public OrderStatisticsVO statistics() {
        return OrderStatisticsVO.builder()
                .toBeConfirmed(orderMapper.selectStatistics(Orders.TO_BE_CONFIRMED))
                .confirmed(orderMapper.selectStatistics(Orders.CONFIRMED))
                .deliveryInProgress(orderMapper.selectStatistics(Orders.DELIVERY_IN_PROGRESS))
                .build();
    }

    /**
     * 支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @Override
    public void payment(OrdersPaymentDTO ordersPaymentDTO) {
        Orders orders = orderMapper.getByNumberAndUserId(ordersPaymentDTO.getOrderNumber());
        Orders order = Orders.builder()
                .id(orders.getId())
                .number(ordersPaymentDTO.getOrderNumber())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now()).build();
        orderMapper.updateById(order);
        Map map = new HashMap();
        map.put("type", 1);//消息类型，1表示来单提醒
        map.put("orderId", orders.getId());
        map.put("content", "订单号：" + ordersPaymentDTO.getOrderNumber());

        //通过WebSocket实现来单提醒，向客户端浏览器推送消息
        webSocketServer.sendToAllClient(JSON.toJSONString(map));
    }
    /**
     * 接订单
     *
     * @param ordersConfirmDTO
     */
    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        Orders order = Orders.builder().id(ordersConfirmDTO.getId())
                .status(Orders.CONFIRMED)
                .build();
        orderMapper.updateById(order);
    }

    /**
     * 拒绝订单
     *
     * @param ordersRejectionDTO
     */
    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        Orders order = Orders.builder().id(ordersRejectionDTO.getId())
                .status(Orders.CANCELLED)
                .rejectionReason(ordersRejectionDTO.getRejectionReason())
                .cancelReason(ordersRejectionDTO.getRejectionReason())
                .cancelTime(LocalDateTime.now())
                .build();
        orderMapper.updateById(order);
    }

    @Override
    public void delivery(Long id) {
        Orders order = Orders.builder().id(id)
                .status(Orders.DELIVERY_IN_PROGRESS)
                .build();
        orderMapper.updateById(order);
    }

    @Override
    public void complete(Long id) {
        Orders order = Orders.builder().id(id)
                .status(Orders.COMPLETED)
                .deliveryTime(LocalDateTime.now())
                .build();
        orderMapper.updateById(order);
    }

    @Override
    public void reminder(Long id) {
        // 查询订单是否存在
        Orders orders = orderMapper.selectByOrderId(id);
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        //基于WebSocket实现催单
        Map map = new HashMap();
        map.put("type", 2);//2代表用户催单
        map.put("orderId", id);
        map.put("content", "订单号：" + orders.getNumber());
        webSocketServer.sendToAllClient(JSON.toJSONString(map));
    }

    public String getGeo(String address) throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("address", address);
        map.put("output", "json");
        map.put("ak", AK);
        String s = HttpClientUtil.doGet(URL_GEO, map);
        ObjectMapper objectMapper = new ObjectMapper();
        if (Integer.parseInt(objectMapper.readTree(s).get("status").toString()) != 0) {
            throw new OrderBusinessException("地址解析失败");
        }
        JsonNode rootNode = objectMapper.readTree(s);
        JsonNode lng = rootNode.get("result").get("location").get("lng");
        JsonNode lat = rootNode.get("result").get("location").get("lat");
        return lat.toString() + "," + lng.toString();
    }

    public void checkOutOfRange(String address) throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("origin", getGeo(LOCAL_ADDRESS));
        map.put("destination", getGeo(address));
        map.put("ak", AK);
        String s = HttpClientUtil.doGet(URL_DIR, map);
        //数据解析
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(s);
        if (!rootNode.get("status").toString().equals("0")) {
            throw new OrderBusinessException("配送路线规划失败");
        }
        JsonNode resultNode = rootNode.get("result").get("routes").get(0).get("distance");
        int distance = Integer.parseInt((resultNode).toString());
        if (distance > 5000) {
            //配送距离超过5000米
            throw new OrderBusinessException("超出配送范围");
        }
        log.info("距离:{}", distance);
    }

}
