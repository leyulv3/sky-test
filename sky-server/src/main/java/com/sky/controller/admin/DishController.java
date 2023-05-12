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

    @PostMapping
    public Result saveDish(@RequestBody DishDTO dishDTO) {
        dishService.saveDish(dishDTO);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult> pageDish(DishPageQueryDTO dishQuery) {
        return Result.success(dishService.pageDish(dishQuery));
    }

    @GetMapping("/{id}")
    public Result<DishVO> selectDish(@PathVariable Long id) {
        return Result.success(dishService.selectDishById(id));
    }


    @DeleteMapping
    public Result deleteDish(List<Long> ids) {
        dishService.deleteDishByIds(ids);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    public Result changeStatus(@PathVariable Integer status, Long id) {
        dishService.changeStatus(status, id);
        return Result.success();
    }
    @PutMapping
    public Result updateDish(@RequestBody DishDTO dishDTO){
        dishService.updateDish(dishDTO);
        return Result.success();
    }
}
