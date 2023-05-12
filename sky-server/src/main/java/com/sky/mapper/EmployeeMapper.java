package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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

    @AutoFill(OperationType.UPDATE)
    boolean updateEmp(Employee employee);

    @Select("select password from employee where id=#{id}")
    String selectEmpPwd(Long empId);

    @AutoFill(OperationType.INSERT)
    void insertEmp(Employee employee);
}
