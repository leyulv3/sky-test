package com.sky.controller.user;

import com.sky.dto.OrdersSubmitDTO;
import com.sky.dto.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    private Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        OrderSubmitVO submit = orderService.submit(ordersSubmitDTO);
        return Result.success(submit);
    }
    @GetMapping("/historyOrders")
    public Result<PageResult> historyOrders(@RequestParam(value = "page",defaultValue = "1") Integer page ,
                                            @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,Integer status){
        PageResult pageResult = orderService.historyOrders(page, pageSize, status);
        return Result.success(pageResult);
    }
}
