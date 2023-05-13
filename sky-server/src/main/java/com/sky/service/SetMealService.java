package com.sky.service;

import com.sky.dto.PageResult;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetMealService {
    PageResult page(SetmealPageQueryDTO pageDto);

    void saveSetMeal(SetmealDTO setmealDTO);

    void removeSetMeals(List<Long> ids);

    SetmealVO getSetMeal(Long id);

    void updateSetMeal(SetmealDTO setmealDTO);

    void changeStatus(Integer status, Long id);
}
