package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetMealDishMapper {
    List<SetmealDish> selectByIds(List<Long> ids);

    void insertSetMealDish(List<SetmealDish> setmealDishes);

    void deleteSetMealDishs(List<Long> ids);
    @Select("select * from setmeal_dish where setmeal_id=#{id}")
    List<SetmealDish> selectSetMealDishById(Long id);
}
