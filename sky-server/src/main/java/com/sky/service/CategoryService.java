package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.dto.PageResult;
import com.sky.entity.Category;

public interface CategoryService {
    PageResult categoryPage(CategoryPageQueryDTO page);

    boolean addCategory(CategoryDTO category);

    void changeStatus(Integer status,Long id);

    void updateEmp(CategoryDTO categoryDTO);

    void deleteEmp(Long id);
}
