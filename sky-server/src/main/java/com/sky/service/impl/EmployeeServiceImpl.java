package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.gson.Gson;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private HttpServletRequest request;


    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        String md5pwd = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!md5pwd.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    @Override
    public PageResult page(EmployeePageQueryDTO queryDTO) {
        PageHelper.startPage(queryDTO.getPage(), queryDTO.getPageSize());
        List<Employee> employees = employeeMapper.selectEmpPage(queryDTO.getName());
        Page<Employee> p = (Page<Employee>) employees;
        return new PageResult(p.getTotal(), p.getResult());
    }

    @Override
    public boolean editPassword(PasswordEditDTO editDTO) {
        Employee employee = new Employee();
        //1、判断密码是否正确
        String pwd = employeeMapper.selectEmpPwd(editDTO.getEmpId());
        log.info("id:{}", editDTO);
        if (pwd == null) return false;
        String password = DigestUtils.md5DigestAsHex(editDTO.getOldPassword().getBytes());
        if (!password.equals(pwd)) return false;
        //3、执行密码修改
        employee.setPassword(DigestUtils.md5DigestAsHex(editDTO.getNewPassword().getBytes()));
        employee.setId(editDTO.getEmpId());
        return employeeMapper.updateEmp(employee);
    }

    @Override
    public boolean updateEmp(Employee employee) {
        return employeeMapper.updateEmp(employee);
    }

    @Override
    public Employee selectById(Long id) {
        return employeeMapper.selectEmpById(id);
    }

    @Override
    public void saveEmp(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        //将DTO中的属性保存到实体类中
        BeanUtils.copyProperties(employeeDTO, employee);
        //设置状态
        employee.setStatus(StatusConstant.DISABLE);
        //设置初始密码
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        //设置创建时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        //设置当前记录创建人id和修改人id
        Long createId = BaseContext.getCurrentId();
        employee.setCreateUser(createId);
        employee.setUpdateUser(createId);
        employeeMapper.insertEmp(employee);
    }

    @Override
    public void changeStatus(Integer status, Long id) {
        Employee employee = new Employee();
        employee.setStatus(status);
        employee.setId(id);
        employeeMapper.updateEmp(employee);
    }
}
