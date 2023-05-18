package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.jsonwebtoken.lang.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Cacheable(value = "DishCache",key = "#p0")
    @GetMapping("/list")
    public Result<List<DishVO>> list(Long categoryId) {
        //构造redis中的key
        String key = "dish_" + categoryId;
        //查询redis中是否有值
        List<DishVO> dishVOS = (List<DishVO>) redisTemplate.opsForValue().get(key);
        //如果存在则直接返回redis中的数据
        if (!Collections.isEmpty(dishVOS)) {
            return Result.success(dishVOS);
        }
        //如果不存在，则从数据库中查询数据并添加到redis中
        Dish dish = Dish.builder().categoryId(categoryId).status(StatusConstant.ENABLE).build();
        List<DishVO> dishes = dishService.listWithFlavor(dish);
        //将数据保存到redis中
        redisTemplate.opsForValue().set(key,dishes);
        return Result.success(dishes);
    }
}
