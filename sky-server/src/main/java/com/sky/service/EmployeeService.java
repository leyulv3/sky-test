package com.sky.service;

import com.sky.dto.*;
import com.sky.entity.Employee;

public interface EmployeeService {

    Employee login(EmployeeLoginDTO employeeLoginDTO);

    PageResult page(EmployeePageQueryDTO employeePageQueryDTO);

    boolean editPassword(PasswordEditDTO editDTO);

    void updateEmp(EmployeeDTO employeeDTO);

    Employee selectById(Long id);

    void saveEmp(EmployeeDTO employee);

    void changeStatus(Integer status, Long id);
}
