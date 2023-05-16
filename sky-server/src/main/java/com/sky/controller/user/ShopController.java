package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;


@RestController("userShopController")
@RequestMapping("/user/shop")
@Api(tags = "状态相关接口")
@Slf4j
public class ShopController {
    @Autowired
    private RedisTemplate redisTemplate;
    private static final String shopStatus = "SHOP_STATUS";

    /**
     * 获取营业状态
     *
     * @return
     */
    @GetMapping("/status")
    public Result<Integer> getStatus() {
        //获取营业状态
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Integer status = (Integer) valueOperations.get(shopStatus);
        return Result.success(status);
    }
}
