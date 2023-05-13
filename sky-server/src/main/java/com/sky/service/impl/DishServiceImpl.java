package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.dto.PageResult;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetMealMapper;
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
    @Autowired
    private SetMealDishMapper setMealDishMapper;
    @Autowired
    private SetMealMapper setMealMapper;

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
        Dish dish = dishMapper.selectById(id);
        List<DishFlavor> dishFlavors = dishFlavorMapper.selectFlavorById(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    @Override
    @Transactional
    public void deleteDishByIds(List<Long> ids) {
        /**
         * - 可以一次删除一个菜品，也可以批量删除菜品
         * - 起售中的菜品不能删除
         * - 被套餐关联的菜品不能删除
         * - 删除菜品后，关联的口味数据也需要删除掉
         */
        //1、起售中的菜品不能删除
        for (Long id : ids) {
            if (dishMapper.selectById(id).getStatus().equals(StatusConstant.ENABLE)) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //2、被套餐关联的菜品不能删除
        if (!CollectionUtils.isEmpty(setMealDishMapper.selectByIds(ids))) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //3、删除菜品后，关联的口味数据删除
        dishMapper.deleteDishByIds(ids);
        dishFlavorMapper.deleteDishByIds(ids);
    }

    @Override
    @Transactional
    public void changeStatus(Integer status, Long id) {
        Dish dish = Dish.builder().status(status).id(id).build();
        dishMapper.updateDish(dish);
        //根据菜品id查询中间表中的套餐id
        List<Long> flavorIds = dishFlavorMapper.selectByDishId(id);
        if (flavorIds != null && flavorIds.size() > 0 && status==0) {
            flavorIds.forEach(flavorId -> {
                Setmeal setmeal = Setmeal.builder().id(flavorId).status(status).build();
                setMealMapper.updateSetMeal(setmeal);
            });
        }
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
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors.size() > 0) {
            flavors.forEach(flavor -> flavor.setDishId(dish.getId()));
            dishFlavorMapper.insertSetmeal(flavors);
        }
    }

    @Override
    public List<DishVO> list(Long categoryId) {
        return dishMapper.selectByCategoryId(categoryId);
    }
}
