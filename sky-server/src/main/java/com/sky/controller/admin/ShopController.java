package com.sky.controller.admin;

import com.github.pagehelper.Page;
import com.sky.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

@RestController("AdminShopController")
@RequestMapping("/admin/shop")
public class ShopController {
    @Autowired
    private RedisTemplate redisTemplate;
    private final static String shopStatus="SHOP_STATUS";
    @PutMapping("/{status}")
    public Result updateStatus(@PathVariable Integer status){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set(shopStatus,status);
        return Result.success();
    }
    @GetMapping("/status")
    public Result getStatus(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        return Result.success(valueOperations.get(shopStatus));
    }
}
