package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    /**
     * 插入口味
     * @param flavors
     */
    void insertSetmeal(List<DishFlavor> flavors);

    void deleteDishByIds(List<Long> ids);
    @Select("select * from dish_flavor where dish_id=#{id}")
    List<DishFlavor> selectFlavorById(Long id);
}
