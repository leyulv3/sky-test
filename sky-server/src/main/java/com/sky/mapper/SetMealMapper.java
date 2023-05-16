package com.sky.mapper;


import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
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

    List<Setmeal> list(Setmeal setmeal);
/**
     * 根据套餐id查询菜品选项
     * @param id
     * @return
     */
    @Select("select sd.name, sd.copies, d.image, d.description " +
            "from setmeal_dish sd left join dish d on sd.dish_id = d.id " +
            "where sd.setmeal_id = #{id}")
    List<DishItemVO> getDishItemBySetmealId(Long id);
}
