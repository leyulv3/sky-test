package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderMapper {

    @Insert("insert into orders (number, user_id, address_book_id, consignee, phone, delivery_status, estimated_delivery_time, pack_amount, amount, remark, tableware_number, tableware_status, status, order_time, pay_status) " +
            " values (#{number},#{userId},#{addressBookId},#{consignee},#{phone},#{deliveryStatus},#{estimatedDeliveryTime},#{packAmount},#{amount},#{remark},#{tablewareNumber},#{tablewareStatus},#{status},#{orderTime},#{payStatus})")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    void insert(Orders orders);

    List<Orders> selectHistoryOrders(Long userId, Integer status);
}
