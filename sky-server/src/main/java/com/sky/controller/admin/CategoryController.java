package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.dto.PageResult;
import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 分页查询分类
     * @param page 分页信息
     * @return 分页结果
     */
    @GetMapping("/page")
    public Result<PageResult> categoryPage(CategoryPageQueryDTO page) {
        return Result.success(categoryService.categoryPage(page));
    }

    /**
     * 添加分类
     * @param category 分类信息
     * @return 添加结果
     */
    @PostMapping()
    public Result<String> addCategory(@RequestBody CategoryDTO category) {
        categoryService.addCategory(category);
        return Result.success();
    }

    /**
     * 修改分类状态
     * @param status 1:启用 0:禁用
     * @param id 分类id
     * @return 修改结果
     */
    @PostMapping("/status/{status}")
    public Result<String> changeStatus(@PathVariable Integer status, Long id) {
        categoryService.changeStatus(status, id);
        return Result.success();
    }

    /**
     * 更新分类
     * @param categoryDTO 分类信息
     * @return 更新结果
     */
    @PutMapping()
    public Result<String> updateEmp(@RequestBody CategoryDTO categoryDTO) {
        categoryService.updateEmp(categoryDTO);
        return Result.success();
    }

    /**
     * 删除分类
     * @param id 分类id
     * @return 删除结果
     */
    @DeleteMapping
    public Result<String> deleteEmp(Long id) {
        categoryService.deleteEmp(id);
        return Result.success();
    }

    /**
     * 查询分类
     * @param type 1:菜单 2:套餐
     * @return 分类列表
     */
    @GetMapping("/list")
    public Result<List<Category>> list(Integer type) {
        return Result.success(categoryService.list(type));
    }

}
