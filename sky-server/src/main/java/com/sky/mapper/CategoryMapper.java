package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {
    /**
     * 分页查询
     *
     * @param name
     * @param type
     * @return
     */
    Page<Category> selectPage(String name, Integer type);

    @Insert("insert into category (type,name,sort,status,create_time,update_time,create_user,update_user) " +
            "values(#{type},#{name},#{sort},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    @AutoFill(OperationType.INSERT)
    void insertCategory(Category category);

    /**
     * 根据id查询
     *
     * @param category
     * @return
     */
    @AutoFill(OperationType.UPDATE)
    void updateCategory(Category category);

    /**
     * 根据id删除
     *
     * @param id
     */
    @Delete("delete from category where id=#{id}")
    void deleteEmp(Long id);

    /**
     * 根据id查询
     *
     * @param type
     * @return
     */
    @Select("select id,name from category where type=#{type} order by sort")
    List<Category> selectByType(Integer type);
}
