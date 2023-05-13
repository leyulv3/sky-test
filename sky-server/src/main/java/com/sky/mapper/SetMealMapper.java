package com.sky.mapper;


import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface SetMealMapper {
    @Select("select count(0) from setmeal where category_id=#{id}")
    int countByCategoryId(Long id);

    List<SetmealVO> selectPage(SetmealPageQueryDTO pageDto);

    @AutoFill(OperationType.INSERT)
    void insertSetMeal(Setmeal setmeal);

    void deleteSetMeals(List<Long> ids);

    @AutoFill(OperationType.UPDATE)
    void updateSetMeal(Setmeal setmeal);

    Integer selectListStatus(List<Long> ids);

    SetmealVO selectById(Long id);
}
