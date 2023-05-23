package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/workspace")
public class WorkSpaceController {
    @Autowired
    private WorkSpaceService workSpaceService;
    @GetMapping("/businessData")
    public Result<BusinessDataVO> businessData(){
        return Result.success(workSpaceService.businessData());
    }
    @GetMapping("/overviewOrders")
    public Result<OrderOverViewVO> overviewOrders(){
        return Result.success(workSpaceService.overviewOrders());
    }
    @GetMapping("/overviewDishes")
    public Result<DishOverViewVO> overviewDishes(){
        return Result.success(workSpaceService.overviewDishes());
    }
    @GetMapping("/overviewSetmeals")
    public Result<SetmealOverViewVO> overviewSeteals(){
        return Result.success(workSpaceService.overviewSeteals());
    }
}
