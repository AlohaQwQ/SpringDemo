package com.example.running.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * @author Aloha
 * @date 2022/12/13 23:10
 * @description ServletContext 监听器， 项目初始化context监听
 */
//@WebListener
public class MyServletContextListener implements ServletContextListener {

    private Logger logger = LoggerFactory.getLogger(MyServletContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("[ {} ] context 监听初始化", sce.getServletContext().getServletContextName());
        ServletContextListener.super.contextInitialized(sce);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("[ {} ] context 监听销毁", sce.getServletContext().getServletContextName());
        ServletContextListener.super.contextDestroyed(sce);
    }
}
