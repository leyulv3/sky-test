package com.sky.mapper;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.dto.PageResult;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {

    List<Category> selectPage(String name, Integer type);
    @Insert("insert into category (type,name,sort,status,create_time,update_time) values(#{type},#{name},#{sort},1,now(),now())")
    int insertCategory(CategoryDTO category);

    void updateCategory(Category category);
    @Delete("delete from category where id=#{id}")
    void deleteEmp(Long id);
}
