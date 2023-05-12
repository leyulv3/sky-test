package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.dto.PageResult;
import com.sky.entity.Dish;
import com.sky.vo.DishVO;
import io.swagger.models.auth.In;

public interface DishService {

    void saveDish(DishDTO dishDTO);

    PageResult pageDish(DishPageQueryDTO dishQuery);

    DishVO selectDishById(Integer id);

    void deleteDishByIds(Integer[] ids);
}
