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
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        if (!CollectionUtils.isEmpty(flavors)) {    //如果有口味
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
    public DishVO selectDishById(Long id) {
        DishVO dishVO = dishMapper.selectById(id);
        List<DishFlavor> dishFlavors = dishFlavorMapper.selectFlavorById(id);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    @Override
    @Transactional
    public void deleteDishByIds(List<Long> ids) {
        dishMapper.deleteDishByIds(ids);
        dishFlavorMapper.deleteDishByIds(ids);
    }

    @Override
    public void changeStatus(Integer status, Long id) {
        Dish dish = Dish.builder().status(status).id(id).build();
        dishMapper.updateDish(dish);
    }

    @Override
    @Transactional
    public void updateDish(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        //更新dish表中的基本数据
        dishMapper.updateDish(dish);
        //删除原有的口味数据
        List<Long> ids = Collections.singletonList(dishDTO.getId());
        dishFlavorMapper.deleteDishByIds(ids);
        //重新插入口味数据
        dishFlavorMapper.insertSetmeal(dishDTO.getFlavors());
    }
}
