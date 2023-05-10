package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex) {
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * SQL异常处理
     * @param ex 异常
     * @return error
     */
    @ExceptionHandler
    public Result exceptionSqlHandler(SQLIntegrityConstraintViolationException ex) {
        log.error("异常信息：{}", ex.getMessage());
        String message = ex.getMessage();
        String[] s = message.split(" ");
        if (message.contains("Duplicate entry")) {
            if (s[5].contains("username")) return Result.error(s[2] + MessageConstant.USER_EXISTED_FOUND);
            return Result.error(s[2] + "已存在");
        }
        return Result.error("未知错误");
    }

}
