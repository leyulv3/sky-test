package com.sky.mapper;

import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {

    @Insert("insert into orders (number, user_id, address_book_id, consignee, phone, delivery_status, estimated_delivery_time, pack_amount, amount, remark, tableware_number, tableware_status, status, order_time, pay_status,address) " +
            " values (#{number},#{userId},#{addressBookId},#{consignee},#{phone},#{deliveryStatus},#{estimatedDeliveryTime},#{packAmount},#{amount},#{remark},#{tablewareNumber},#{tablewareStatus},#{status},#{orderTime},#{payStatus},#{address})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Orders orders);

    List<Orders> selectHistoryOrders(Long userId, Integer status);

    Orders selectByOrderId(Long id);

    void updateById(Orders orders);

    List<OrderVO> selectSearch(OrdersPageQueryDTO queryDTO);

    Integer selectStatistics(Integer status);


    List<Orders> selectOverOrder(LocalDateTime orderTime, Integer status);

    @Select("select sum(amount) from orders where order_time between #{begin} and #{end} and status=#{status} ")
    Double sumByMap(Map map);

    int getTotal(Map map);

    @Select("SELECT name,sum(od.number)  as number from order_detail od ,orders o where o.id=od.order_id and o.status = 5 and order_time between #{startTime} and #{endTime} group by name order by number desc limit 0, 10")
    List<GoodsSalesDTO> selectMap(Map map);
    @Select("select * from orders where number=#{outTradeNo}")
    Orders getByNumberAndUserId(String outTradeNo);
}
