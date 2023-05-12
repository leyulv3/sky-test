package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.dto.PageResult;
import com.sky.entity.Category;

import java.util.List;

public interface CategoryService {
    PageResult categoryPage(CategoryPageQueryDTO page);

    void addCategory(CategoryDTO category);

    void changeStatus(Integer status,Long id);

    void updateEmp(CategoryDTO categoryDTO);

    void deleteEmp(Long id);


    List<Category> list(Integer type);
}
