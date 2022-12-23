package com.example.running.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Aloha
 * @date 2022/12/13 18:10
 * @description 自定义异常解析器, 并置于最高优先级
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class CustomHandlerExceptionResolver implements HandlerExceptionResolver {

    private Logger logger = LoggerFactory.getLogger(CustomHandlerExceptionResolver.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        logger.info("自定义异常解析:{}", ex.toString());

        try {
            response.sendError(666, "服务器炸了");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new ModelAndView();
    }
}
