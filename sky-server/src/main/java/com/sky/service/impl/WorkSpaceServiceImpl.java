package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Optional;

@Service
public class WorkSpaceServiceImpl implements WorkSpaceService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetMealMapper setMealMapper;
    @Override
    public BusinessDataVO businessData() {
        HashMap<String, Object> map = new HashMap<>();
        LocalDateTime begin = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        map.put("begin", begin);
        map.put("end", end);
        map.put("status", Orders.COMPLETED);
        //营业额
        Double turnover = Optional.ofNullable(orderMapper.sumByMap(map)).orElse(0.0);
        //有效订单数
        Integer validOrderCount = orderMapper.getTotal(map);
        //订单完成率
        map.remove("status");
        //总订单数
        Integer total = orderMapper.getTotal(map);
        double orderCompletionRate = 0.0;
        if (validOrderCount != null && total != null) {
            orderCompletionRate = validOrderCount / total;
        }
        //平均客单价
        double unitPrice = 0.0;
        if (turnover != null && validOrderCount != null) {
            unitPrice = turnover / validOrderCount;
        }
        //新增用户数
        Integer newUsers = userMapper.selectCountNewUser(begin, end);
        return BusinessDataVO.builder()
                .turnover(turnover)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .newUsers(newUsers)
                .build();
    }

    //    待接单数量
    //    private Integer waitingOrders;
    //    //待派送数量
    //    private Integer deliveredOrders;
    //    //已完成数量
    //    private Integer completedOrders;
    //    //已取消数量
    //    private Integer cancelledOrders;
    //    //全部订单
    //    private Integer allOrders;
    @Override
    public OrderOverViewVO overviewOrders() {

        //全部订单
        Integer allOrders = orderMapper.selectStatistics(null);
        //已取消数量
        Integer cancelledOrders = orderMapper.selectStatistics(Orders.CANCELLED);
        //待接单数量
        Integer waitingOrders = orderMapper.selectStatistics(Orders.TO_BE_CONFIRMED);
        //待派送数量
        Integer deliveredOrders = orderMapper.selectStatistics(Orders.DELIVERY_IN_PROGRESS);
        //已完成数量
        Integer completedOrders = orderMapper.selectStatistics(Orders.COMPLETED);
        return OrderOverViewVO.builder()
                .waitingOrders(waitingOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .deliveredOrders(deliveredOrders)
                .allOrders(allOrders)
                .build();
    }

    @Override
    public DishOverViewVO overviewDishes() {
        //禁用
        Integer disable = dishMapper.countDish(StatusConstant.DISABLE);
        //启用
        Integer enable = dishMapper.countDish(StatusConstant.ENABLE);
        return DishOverViewVO.builder()
                .discontinued(disable)
                .sold(enable)
                .build();
    }

    @Override
    public SetmealOverViewVO overviewSeteals() {
        //禁用
        Integer disable = setMealMapper.countDish(StatusConstant.DISABLE);
        //启用
        Integer enable = setMealMapper.countDish(StatusConstant.ENABLE);
        return SetmealOverViewVO.builder()
                .discontinued(disable)
                .sold(enable)
                .build();
    }
}
