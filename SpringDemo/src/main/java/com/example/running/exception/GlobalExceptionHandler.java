package com.example.running.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author Aloha
 * @date 2022/12/13 15:50
 * @description 处理整个web controller 的异常
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * @author Aloha
     * @date 2022/12/13 15:56
     * @description ExceptionHandler 指定处理异常数组, 返回 ModelAndView 或 viewname
     */
    @ExceptionHandler({ArithmeticException.class, NullPointerException.class})
    public String handlerException(Exception e){
        logger.info("自定义异常捕获:{}", e.getMessage());
        //返回视图地址
        return "login";
    }
}
