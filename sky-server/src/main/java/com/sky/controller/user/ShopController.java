package com.sky.controller.user;

import com.sky.result.Result;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController("UserShopController")
@RequestMapping("/user/shop")
public class ShopController {
    @Autowired
    private RedisTemplate redisTemplate;
    private final static String shopStatus = "SHOP_STATUS";

    @GetMapping("/status")
    public Result getStatus() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        return Result.success(valueOperations.get(shopStatus));
    }

    @GetMapping()
    public Result getName() {
        return Result.success("成功");
    }

    @PostMapping()
    public Result postShop(@RequestBody Map<String, Object> data) {
        int id = (int) data.get("id");
        String name = (String) data.get("name");
        // 处理请求，返回响应
        return Result.success("id=" + id + ", name=" + name);
    }
}
