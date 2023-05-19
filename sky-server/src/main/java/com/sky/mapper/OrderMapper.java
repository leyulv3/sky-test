package com.sky.mapper;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {

    @Insert("insert into orders (number, user_id, address_book_id, consignee, phone, delivery_status, estimated_delivery_time, pack_amount, amount, remark, tableware_number, tableware_status, status, order_time, pay_status,address) " +
            " values (#{number},#{userId},#{addressBookId},#{consignee},#{phone},#{deliveryStatus},#{estimatedDeliveryTime},#{packAmount},#{amount},#{remark},#{tablewareNumber},#{tablewareStatus},#{status},#{orderTime},#{payStatus},#{address})")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    void insert(Orders orders);

    List<Orders> selectHistoryOrders(Long userId, Integer status);

    Orders selectByOrderId(Long id);

    void updateById(Orders orders);

    List<OrderVO> selectSearch(OrdersPageQueryDTO queryDTO);

    Integer selectStatistics(Integer status);
}
