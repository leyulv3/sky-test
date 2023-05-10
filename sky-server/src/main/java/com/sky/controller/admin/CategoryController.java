package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 分页
     */
    @GetMapping("/page")
    public Result categoryPage(CategoryPageQueryDTO page){
        log.info("page:{}",page);
        return Result.success(categoryService.categoryPage(page));
    }

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping()
    public Result addCategory(@RequestBody CategoryDTO category){
        return Result.success(categoryService.addCategory(category));
    }
    /**
     * 更改状态
     */
    @PostMapping("/status/{status}")
    public Result changeStatus(@PathVariable Integer status, Long id){
        categoryService.changeStatus(status,id);
        return Result.success();
    }
    /**
     * 修改分类
     */
    @PutMapping()
    public Result updateEmp(@RequestBody CategoryDTO categoryDTO){
        categoryService.updateEmp(categoryDTO);
        return Result.success();
    }
    /**
     * 删除分类
     */
    @DeleteMapping
    public Result deleteEmp(Long id){
        categoryService.deleteEmp(id);
        return Result.success();
    }
}
