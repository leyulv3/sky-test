package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.dto.PageResult;
import com.sky.entity.Dish;
import com.sky.vo.DishVO;
import io.swagger.models.auth.In;

import java.util.List;

public interface DishService {

    void saveDish(DishDTO dishDTO);

    PageResult pageDish(DishPageQueryDTO dishQuery);

    DishVO selectDishById(Long id);

    void deleteDishByIds(List<Long> ids);

    void changeStatus(Integer status,Long id);

    void updateDish(DishDTO dishDTO);
}
