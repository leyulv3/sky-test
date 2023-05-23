package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

@Mapper
public interface UserMapper {
    @Select("select * from user where openid=#{openid}")
    User selectByOpenId(String openId);
    void insertUser(User newUser);
    @Select("select count(*) from user where create_time between #{startTime} and #{endTime}")
    Integer selectCountNewUser(LocalDateTime startTime, LocalDateTime endTime);
    @Select("select count(*) from user")
    Integer selectCountUser();
}
