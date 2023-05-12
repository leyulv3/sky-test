package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.dto.PageResult;
import com.sky.entity.Dish;
import com.sky.mapper.DishMapper;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;

    @PostMapping
    public Result save() {


        return Result.success();
    }


    @Override
    public void saveDish(DishDTO dishDTO) {
        //业务逻辑
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dish.setStatus(StatusConstant.DISABLE);
        dish.setUpdateTime(LocalDateTime.now());
        dish.setCreateTime(LocalDateTime.now());
        dish.setCreateUser(BaseContext.getCurrentId());
        dish.setUpdateUser(BaseContext.getCurrentId());
        dishMapper.insertDish(dish);
    }

    @Override
    public PageResult pageDish(DishPageQueryDTO dishQuery) {
        PageHelper.startPage(dishQuery.getPage(), dishQuery.getPageSize());
        Page<DishVO> page = (Page<DishVO>) dishMapper.selectDishPage(dishQuery);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public DishVO selectDishById(Integer id) {
        return dishMapper.selectById(id);
    }
}
