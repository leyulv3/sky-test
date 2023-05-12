package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {
    @Select("select count(0) from setmeal where category_id=#{id}")
    int countByCategoryId(Long id);


    void insertSetmeal(Long id, List<List<DishFlavor>> lists);
}
