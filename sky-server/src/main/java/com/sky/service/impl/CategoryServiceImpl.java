package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.dto.PageResult;
import com.sky.entity.Category;
import com.sky.mapper.CategoryMapper;
import com.sky.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public PageResult categoryPage(CategoryPageQueryDTO page) {
        PageHelper.startPage(page.getPage(), page.getPageSize());
        Page<Category> category = (Page<Category>) categoryMapper.selectPage(page.getName(), page.getType());
        return new PageResult(category.getTotal(),category.getResult());
    }

    @Override
    public boolean addCategory(CategoryDTO category) {
        return categoryMapper.insertCategory(category) == 1;
    }

    @Override
    public void changeStatus(Integer status , Long id) {
        Category category = new Category();
        category.setStatus(status);
        category.setId(id);
        categoryMapper.updateCategory(category);
    }

    @Override
    public void updateEmp(Category category) {
        categoryMapper.updateCategory(category);
    }

    @Override
    public void deleteEmp(Long id) {
        categoryMapper.deleteEmp(id);
    }
}
