package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.*;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api("员工管理")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 添加员工
     * @param employeeLoginDTO 员工信息
     * @return 添加结果
     */
    @ApiOperation("登录")
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        Employee employee = employeeService.login(employeeLoginDTO);
        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();
        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return Result
     */
    @ApiOperation("退出")
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 分页
     *
     * @param pageDto 分页参数
     * @return Result<PageResult>
     */
    @ApiOperation("分页")
    @GetMapping("/page")
    public Result<PageResult> page(EmployeePageQueryDTO pageDto) {
        return Result.success(employeeService.page(pageDto));
    }

    /**
     * 修改密码
     *
     * @param editDTO 修改密码参数
     * @return Result
     */
    @ApiOperation("修改密码")
    @PutMapping("/editPassword")
    public Result editPassword(@RequestBody PasswordEditDTO editDTO) {
        return employeeService.editPassword(editDTO) ? Result.success() : Result.error("修改失败");
    }

    /**
     * 修改信息
     *
     * @param employeeDTO 修改信息参数
     * @return Result
     */
    @ApiOperation("修改信息")
    @PutMapping()
    public Result updateEmp(@RequestBody EmployeeDTO employeeDTO) {
        employeeService.updateEmp(employeeDTO);
        return Result.success();
    }

    /**
     * 根据id查询信息
     *
     * @param id id
     * @return Result<Employee>
     */
    @ApiOperation("根据id查询信息")
    @GetMapping("/{id}")
    public Result<Employee> selectById(@PathVariable Long id) {
        return Result.success(employeeService.selectById(id));
    }

    /**
     * 新增用户
     *
     * @param employee 用户信息
     * @return Result
     */
    @ApiOperation("新增用户")
    @PostMapping()
    public Result saveEmp(@RequestBody EmployeeDTO employee) {
        employeeService.saveEmp(employee);
        return Result.success();
    }

    /**
     * 更改状态
     *
     * @param status 状态
     * @param id     id
     * @return Result
     */
    @ApiOperation("更改状态")
    @PostMapping("/status/{status}")
    public Result changeStatus(@PathVariable Integer status, Long id) {
        employeeService.changeStatus(status, id);
        return Result.success();
    }

}
