package com.sky.mapper;

import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     *
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    List<Employee> selectEmpPage(String name);

    @Select("select * from employee where id=#{id}")
    Employee selectEmpById(Long id);


    boolean updateEmp(Employee employee);

    @Select("select password from employee where id=#{id}")
    String selectEmpPwd(Long empId);

    void insertEmp(Employee employee);
}
