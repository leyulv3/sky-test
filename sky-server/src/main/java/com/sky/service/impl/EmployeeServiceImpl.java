package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.gson.Gson;
import com.sky.annotation.AutoFill;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
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
        //使用分页插件，传入page、pageSize字段
        PageHelper.startPage(queryDTO.getPage(), queryDTO.getPageSize());
        //根据传入条件进行查询
        List<Employee> employees = employeeMapper.selectEmpPage(queryDTO.getName());
        Page<Employee> p = (Page<Employee>) employees;
        //将查询结果封存到PageResult中返回结果
        return new PageResult(p.getTotal(), p.getResult());
    }

    @Override
    public boolean editPassword(PasswordEditDTO editDTO) {

        //1、判断密码是否正确
        //查询密码
        String pwd = employeeMapper.selectEmpPwd(editDTO.getEmpId());
        //如果没有查询到密码，返回false代表查询失败
        if (pwd == null) return false;
        //将传入旧密码进行md5加密
        String password = DigestUtils.md5DigestAsHex(editDTO.getOldPassword().getBytes());
        //将加密的密码与数据库查询到的密码进行比对
        if (!password.equals(pwd)) return false;
        //2、执行密码修改
        Employee employee = Employee.builder()
                .password(DigestUtils.md5DigestAsHex(editDTO.getNewPassword().getBytes()))
                .id(editDTO.getEmpId()).build();
        return employeeMapper.updateEmp(employee);
    }

    @Override
    public boolean updateEmp(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO,employee);
        employee.setUpdateUser(BaseContext.getCurrentId());
        employee.setUpdateTime(LocalDateTime.now());
        return employeeMapper.updateEmp(employee);
    }

    @Override
    public Employee selectById(Long id) {
        return employeeMapper.selectEmpById(id);
    }

    @Override
    public void saveEmp(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        //1、将DTO中的属性复制到实体类中
        BeanUtils.copyProperties(employeeDTO, employee);
        //2、设置状态
        employee.setStatus(StatusConstant.DISABLE);
        //3、设置初始密码
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        //4、设置创建时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        //5、设置当前记录创建人id和修改人id
        Long createId = BaseContext.getCurrentId();
        employee.setCreateUser(createId);
        employee.setUpdateUser(createId);
        employeeMapper.insertEmp(employee);
    }

    @Override
    public void changeStatus(Integer status, Long id) {
        Employee employee = Employee.builder().status(status).id(id).build();
        employeeMapper.updateEmp(employee);
    }
}
