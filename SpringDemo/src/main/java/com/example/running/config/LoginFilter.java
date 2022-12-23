package com.example.running.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

//@WebFilter(urlPatterns = "/*", filterName = "LoginFilter")
public class LoginFilter implements Filter {

    private Logger logger = LoggerFactory.getLogger(LoginFilter.class);

    //添加排除路径
    private Set<String> excludePatterns = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList("/", "/login")));

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("[ {} ] 登录过滤器初始化", this.getClass().getSimpleName());
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        logger.info("[ {} ] 执行", this.getClass().getSimpleName());

        /*HttpServletRequest httpRequest = ((HttpServletRequest)request);
        //登录逻辑检查
        HttpSession session = httpRequest.getSession();

        //获取当前请求路径信息
        String requestURI = httpRequest.getRequestURI();
        //匹配路径是否放行
        if(excludePatterns.contains(requestURI)){
            chain.doFilter(request, response);
            return;
        }

        Object loginUser = session.getAttribute("loginUser");
        if(loginUser==null){
            //登录拦截
            //session.setAttribute("msg","登录过期");
            //重定向到首页
            //response.sendRedirect("/");

            //使用重定向redirect会导致 request之前放的数据丢失，而使用转发 forward则不会
            request.setAttribute("msg","登录过期");
            request.getRequestDispatcher("/").forward(request, response);
            return;
        }*/

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        logger.info("[ {} ] 摧毁", this.getClass().getSimpleName());
        Filter.super.destroy();
    }
}
