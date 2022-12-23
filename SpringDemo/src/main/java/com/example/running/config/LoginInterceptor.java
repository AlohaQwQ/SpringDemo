package com.example.running.config;

import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.logging.Logger;

/**
 * @author Aloha
 * @date 2022/12/8 0:03
 * @description
 * 登录检查
 * 1.配置好拦截器要拦截哪些请求
 * 2.注册拦截器
 */
public class LoginInterceptor implements HandlerInterceptor {

    /**
     * 目标方法执行前
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler chosen handler to execute, for type and/or instance evaluation
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LoggerFactory.getLogger(LoginInterceptor.class).info("————执行前———— {}", request.getRequestURL());
        //登录逻辑检查
        HttpSession session = request.getSession();
        Object loginUser = session.getAttribute("loginUser");
        /*if(loginUser==null){
            //登录拦截
            //session.setAttribute("msg","登录过期");
            //重定向到首页
            //response.sendRedirect("/");

            //使用重定向redirect会导致 request之前放的数据丢失，而使用转发 forward则不会
            request.setAttribute("msg","登录过期");
            request.getRequestDispatcher("/").forward(request, response);

            return false;
        }*/
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    /**
     * 目标方法执行完成后
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler the handler (or {@link HandlerMethod}) that started asynchronous
     * execution, for type and/or instance examination
     * @param modelAndView the {@code ModelAndView} that the handler returned
     * (can also be {@code null})
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        LoggerFactory.getLogger(LoginInterceptor.class).info("————方法执行完毕————{}", request.getRequestURL());
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    /**
     * 页面渲染完成后
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler the handler (or {@link HandlerMethod}) that started asynchronous
     * execution, for type and/or instance examination
     * @param ex any exception thrown on handler execution, if any; this does not
     * include exceptions that have been handled through an exception resolver
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        LoggerFactory.getLogger(LoginInterceptor.class).info("————页面渲染异常————{}", ex!=null? ex.getMessage(): null);
        LoggerFactory.getLogger(LoginInterceptor.class).info("————页面渲染后————{}", request.getRequestURL());
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
