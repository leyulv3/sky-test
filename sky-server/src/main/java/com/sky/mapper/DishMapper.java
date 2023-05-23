package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {
    @Select("select count(0) from dish where category_id=#{id}")
    int countByCategoryId(Long id);
    @AutoFill(OperationType.INSERT)
    void insertDish(Dish dish);

    List<DishVO> selectDishPage(DishPageQueryDTO dishQuery);
    @Select("select * from dish where id=#{id}")
    Dish selectById(Long id);

    void deleteDishByIds(List<Long> ids);
    @AutoFill(OperationType.UPDATE)
    void updateDish(Dish dish);
    @Select("select * from dish where category_id=#{categoryId}")
    List<DishVO> selectByCategoryId(Long categoryId);

    List<Dish> selectByIds(List<Long> ids);

    List<DishVO> selectWithFlavor(Dish dish);
    @Select("select count(*) from dish where status=#{status}")
    int countDish(Integer status);
}
