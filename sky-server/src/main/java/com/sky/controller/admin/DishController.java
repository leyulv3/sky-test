package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.dto.PageResult;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    /**
     * 添加菜品
     *
     * @param dishDTO 菜品信息
     * @return 添加结果
     */
    @PostMapping
    public Result saveDish(@RequestBody DishDTO dishDTO) {
        dishService.saveDish(dishDTO);
        return Result.success();
    }

    /**
     * 分页查询菜品
     *
     * @param dishQuery 分页信息
     * @return 分页结果
     */
    @GetMapping("/page")
    public Result<PageResult> pageDish(DishPageQueryDTO dishQuery) {
        return Result.success(dishService.pageDish(dishQuery));
    }

    /**
     * 根据id查询菜品
     *
     * @param id 菜品id
     * @return 菜品信息
     */
    @GetMapping("/{id}")
    public Result<DishVO> selectDish(@PathVariable Long id) {
        return Result.success(dishService.selectDishById(id));
    }

    /**
     * 删除菜品
     *
     * @param ids 菜品id集合
     * @return 删除结果
     */
    @DeleteMapping
    public Result deleteDish(@RequestParam List<Long> ids) {
        dishService.deleteDishByIds(ids);
        return Result.success();
    }

    /**
     * 修改菜品状态
     *
     * @param status 1:启用 0:禁用
     * @param id     菜品id
     * @return 修改结果
     */
    @PostMapping("/status/{status}")
    public Result changeStatus(@PathVariable Integer status, Long id) {
        dishService.changeStatus(status, id);
        return Result.success();
    }

    /**
     * 更新菜品
     *
     * @param dishDTO 菜品信息
     * @return 更新结果
     */
    @PutMapping
    public Result updateDish(@RequestBody DishDTO dishDTO) {
        dishService.updateDish(dishDTO);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<DishVO>> list(Long categoryId) {
        return Result.success(dishService.list(categoryId));
    }

}
