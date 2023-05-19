package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface OrderDetailMapper {
    @Insert("insert into order_detail(name, order_id, dish_id, setmeal_id, dish_flavor, number, amount, image) values(#{name}, #{orderId}, #{dishId}, #{setmealId}, #{dishFlavor}, #{number}, #{amount}, #{image})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(OrderDetail cart);
    @Select("select * from order_detail where order_id=#{orderId}")
    List<OrderDetail> getByOrderId(Long orderId);

    void insertList(List<OrderDetail> orderDetails);
}
