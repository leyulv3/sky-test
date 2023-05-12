package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.dto.PageResult;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;


    @Override
    @Transactional
    public void saveDish(DishDTO dishDTO) {
        //业务逻辑
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dish.setStatus(StatusConstant.DISABLE);
        dishMapper.insertDish(dish);
        log.info("dishId:{}", dish.getId());
        //插入口味
        List<DishFlavor> flavors = dishDTO.getFlavors();//前端传过来的口味
        if (flavors != null && flavors.size() > 0) {    //如果有口味
            flavors.forEach(flavor -> {                 //遍历口味
                flavor.setDishId(dish.getId());         //设置dishId
            });
            dishFlavorMapper.insertSetmeal(flavors);    //插入口味
        }
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

    @Override
    @Transactional
    public void deleteDishByIds(Integer[] ids) {
        dishMapper.deleteDishByIds(ids);
        dishFlavorMapper.deleteDishByIds(ids);
    }
}
