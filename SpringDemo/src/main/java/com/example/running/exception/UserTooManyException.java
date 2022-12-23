package com.example.running.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Aloha
 * @date 2022/12/13 16:22
 * @description 自定义异常, 使用 @ResponseStatus 注解，指定返回异常的状态码以及错误信息
 */
@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "用户太多")
public class UserTooManyException extends RuntimeException{

    private Logger logger = LoggerFactory.getLogger(UserTooManyException.class);

    public UserTooManyException() {}

    public UserTooManyException(String message) {
        super(message);
        logger.info("用户太多炸了");
    }
}
