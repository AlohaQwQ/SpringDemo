package com.example.running.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Aloha
 * @date 2022/12/6 0:19
 * @description View 视图解析
 */
@Controller
public class ViewController {


    /**
     * @author Aloha
     * @date 2022/12/6 0:33
     * @description view视图解析
     */
    @GetMapping("/index-thymeleaf")
    public String goIndexThymeleaf(Model model){
        //model中的数据会被放在请求域中 request.setAttribute()
        model.addAttribute("title", "使用thymeleaf模板");
        model.addAttribute("linkTitle", "小破站");
        model.addAttribute("link", "http://www.bilibili.com");
        //跳转到页面
        return "index-thymeleaf";
    }


}
