package com.example.running.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Aloha
 * @date 2022/11/24 18:10
 * @description Goto
 */
@Controller
//@RestController
public class GotoController {

    private Enumeration<String> attributeNames;

    @GetMapping("/goto")
    public String gotoCar(HttpServletRequest request){
        //设置请求域request属性
        request.setAttribute("msg","success...");
        request.setAttribute("code","0");
        //转发到 gotoSuccess请求
        return "forward:/gotoSuccess";
    }

    @ResponseBody
    @GetMapping("/gotoSuccess")
    public Map<String,Object> successCar(@RequestAttribute(value = "msg", required = false) String msg,
                                         HttpServletRequest request){
        Map<String,Object> map = new HashMap<>();
        //请求属性，获取request域中属性，通过用于页面转发时保存数据
        map.put("msg", msg);
        //map.put("code", request.getAttribute("code"));
        map.put("map", request.getAttribute("map"));
        map.put("model", request.getAttribute("model"));
        map.put("request", request.getAttribute("request"));
        map.put("cookie", request.getAttribute("cookie"));
        map.put("cookie1", request.getCookies());
        map.put("locale", request.getLocale());
        return map;
    }

    @GetMapping("/getModel")
    public String getModel(Map<String, Object> map,
                           Model model,
                           HttpServletRequest httpServletRequest,
                           HttpServletResponse httpServletResponse){
        map.put("map", "map666");
        model.addAttribute("model", "model666");
        httpServletRequest.setAttribute("request", "request666");
        Cookie cookie = new Cookie("cookie", "cookie666");
        httpServletResponse.addCookie(cookie);

        //设置请求域request属性
        //request.setAttribute("msg","success...");
        //request.setAttribute("code","0");
        //转发到 gotoSuccess请求
        return "forward:/gotoSuccess";
    }

}
