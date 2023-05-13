package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.PageResult;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.service.SetMealService;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetMealServiceImpl implements SetMealService {
    @Autowired
    private SetMealMapper setMealMapper;
    @Autowired
    private SetMealDishMapper setMealDishMapper;
    @Autowired
    private DishMapper dishMapper;

    /**
     * 分页查询
     *
     * @param pageDto
     * @return
     */
    @Override
    public PageResult page(SetmealPageQueryDTO pageDto) {
        PageHelper.startPage(pageDto.getPage(), pageDto.getPageSize());
        Page<SetmealVO> page = (Page<SetmealVO>) setMealMapper.selectPage(pageDto);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 新增套餐
     *
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void saveSetMeal(SetmealDTO setmealDTO) {
        //1、插入套餐表
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmeal.setStatus(StatusConstant.DISABLE);
        setMealMapper.insertSetMeal(setmeal);
        //2、插入套餐菜品表
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        Long id = setmeal.getId();
        setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(id));
        setMealDishMapper.insertSetMealDish(setmealDishes);
    }

    /**
     * 删除套餐
     *
     * @param ids
     */
    @Override
    @Transactional
    public void removeSetMeals(List<Long> ids) {
        //套餐删除
        //查询套餐是否正在出售
        if (ids != null && ids.size() == setMealMapper.selectListStatus(ids)) {
            //1、删除setmeal
            setMealMapper.deleteSetMeals(ids);
            //2、删除setmealDish表中的数据
            setMealDishMapper.deleteSetMealDishs(ids);
            return;
        }
        throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
    }

    /**
     * 根据id查询套餐数据
     *
     * @param id
     * @return
     */
    @Override
    public SetmealVO getSetMeal(Long id) {
        //查询套餐表中的数据
        SetmealVO setmealVO = setMealMapper.selectById(id);
        //根据套餐id查询中间表中的菜品数据
        List<SetmealDish> setmealDishes = setMealDishMapper.selectSetMealDishById(id);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /**
     * 更新套餐
     *
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void updateSetMeal(SetmealDTO setmealDTO) {
        List<Long> ids = new ArrayList<>();
        ids.add(setmealDTO.getId());
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        //更新套餐表
        setMealMapper.updateSetMeal(setmeal);
        //删除关系表中的相关数据
        setMealDishMapper.deleteSetMealDishs(ids);
        //添加关系表
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        Long id = setmeal.getId();
        setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(id));
        setMealDishMapper.insertSetMealDish(setmealDishes);
    }

    @Override
    @Transactional
    public void changeStatus(Integer status, Long id) {
        //判断当前套餐是否有未启用菜品
        List<SetmealDish> setmealDishes = setMealDishMapper.selectSetMealDishById(id);
        //根据状态判断是否要进行菜品状态的判断
        if (status == 1) {
            //循环判断查看菜品状态是否有未启售
            setmealDishes.forEach(setmealDish -> {
                Dish dish = dishMapper.selectById(setmealDish.getDishId());
                if (dish != null && dish.getStatus() == 0)
                    throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ENABLE_FAILED);
            });
        }
        Setmeal setmeal = Setmeal.builder().status(status).id(id).build();
        setMealMapper.updateSetMeal(setmeal);
    }
}
