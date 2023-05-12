package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.dto.PageResult;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {

    Page<Category> selectPage(String name, Integer type);
    @Insert("insert into category (type,name,sort,status,create_time,update_time,create_user,update_user) " +
            "values(#{type},#{name},#{sort},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    void insertCategory(Category category);

    void updateCategory(Category category);
    @Delete("delete from category where id=#{id}")
    void deleteEmp(Long id);
    @Select("select id,name from category where type=#{type} order by sort")
    List<Category> selectByType(Integer type);
}
