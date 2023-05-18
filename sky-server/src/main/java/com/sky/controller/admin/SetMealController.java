package com.sky.controller.admin;

import com.sky.dto.PageResult;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.Result;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
public class SetMealController {
    @Autowired
    private SetMealService setMealService;
    /**
     * - 新增套餐
     * - 套餐分页查询
     * - 删除套餐
     * - 修改套餐
     * - 起售停售套餐
     */
    /**
     * 套餐分页查询
     *
     * @param pageDto 分页查询条件
     * @return 分页结果
     */
    @GetMapping("/page")
    public Result<PageResult> page(SetmealPageQueryDTO pageDto) {
        return Result.success(setMealService.page(pageDto));
    }

    /**
     * 新增套餐
     *
     * @param setmealDTO 套餐信息
     * @return 操作结果
     */
    @CacheEvict(value = "setmealCache", key = "#p0.categoryId")
    @PostMapping
    public Result<String> saveSetMeal(@RequestBody SetmealDTO setmealDTO) {
        setMealService.saveSetMeal(setmealDTO);
        return Result.success();
    }

    /**
     * 删除套餐
     *
     * @param ids 套餐id集合
     * @return 操作结果
     */
    @CacheEvict(value = "setmealCache", allEntries = true)
    @DeleteMapping
    public Result removeSetMeals(@RequestParam("ids") List<Long> ids) {
        setMealService.removeSetMeals(ids);
        return Result.success();
    }

    //套餐回显
    @GetMapping("/{id}")
    public Result<SetmealVO> getSetMeal(@PathVariable("id") Long id) {
        return Result.success(setMealService.getSetMeal(id));
    }

    //套餐修改
    @CacheEvict(value = "setmealCache", key = "#p0.categoryId")
    @PutMapping
    public Result updateSetMeal(@RequestBody SetmealDTO setmealDTO) {
        setMealService.updateSetMeal(setmealDTO);
        return Result.success();
    }

    //更改状态
    @PostMapping("/status/{status}")
    @CacheEvict(value = "setmealCache", key = "#id")
    public Result changeStatus(@PathVariable Integer status, Long id) {
        setMealService.changeStatus(status, id);
        return Result.success();
    }

}
