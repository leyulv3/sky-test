package com.sky.service;

import com.sky.dto.*;
import com.sky.entity.Employee;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    PageResult page(EmployeePageQueryDTO employeePageQueryDTO);

    boolean editPassword(PasswordEditDTO editDTO);

    boolean updateEmp(Employee employee);

    Employee selectById(Long id);

    void saveEmp(EmployeeDTO employee);

    void changeStatus(Integer status, Long id);
}
