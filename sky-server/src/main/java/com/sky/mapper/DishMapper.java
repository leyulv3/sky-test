package com.sky.mapper;

import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {
    @Select("select count(0) from dish where category_id=#{id}")
    int countByCategoryId(Long id);

    void insertDish(Dish dish);

    List<DishVO> selectDishPage(DishPageQueryDTO dishQuery);
    @Select("select * from dish where id=#{id}")
    DishVO selectById(Integer id);

}
