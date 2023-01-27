package com.example.running.controller;

import com.example.running.annotations.Person;
import com.example.running.bean.Dog;
import com.example.running.bean.Zhouzhou;
import com.stat.auto.service.AutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HttpServletBean;
import org.springframework.web.util.UrlPathHelper;

import javax.annotation.Resource;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Aloha
 * @date 2022/7/24 17:55
 * @description 控制器
 */
//@Profile("test")
@RestController
public class RunningController {

    @Resource
    public Zhouzhou zhouzhou;

    @Autowired
    private Dog dog;

    @Value("${mydog.name}")
    String dogName;

    @Autowired
    AutoService autoService;

    @RequestMapping("/dog")
    public Dog dog(){
        return dog;
    }

    @RequestMapping("/hello")
    public String hello(@RequestParam String hello){
        /*zhouzhou.getCat();
        String name = zhouzhou.getName();
        return "hello:" + dogName + "-" + hello;*/
        return autoService.helloService(hello);
    }

    @RequestMapping("/helloSession")
    public String helloSession(HttpSession session){
        return "hello:" + session;
    }

    @RequestMapping("/hellooModel")
    public String helloModel(Model model){
        //获取请求域中的参数
        model.getAttribute("");
        return "hello:" + model;
    }

    @RequestMapping("/helloPerson")
    public String helloPerson(Person person){
        //使用Bean对象做为请求参
        return "hello:" + person.toString();
    }

    // car/2/owner/zhangsan @PathVariable 获取url路径变量上的参数，参数也可存放在Map<String,String>中
    @GetMapping("/getCar/{id}/owner/{username}")
    public Map<String,Object> getCar(@PathVariable("id") Integer carId,
                                     @PathVariable("username") HttpServletRequest name,
                                     @PathVariable Model pv,
                                     @RequestHeader("host") String headerHost,
                                     @RequestHeader("Referer") String headerOrigin,
                                     @RequestHeader Map<String,String> headerMap,
                                     @RequestParam("id") Integer id,
                                     @RequestParam("ints") List<String> paramList,
                                     @RequestParam Map<String,String> paramMap){
        Map<String,Object> map = new HashMap<>();
        map.put("car1", "新车1-" + carId + "|车主-" + name);
        //map.put("car1-copy", "新车1copy-" + pv.get("id") + "|车主-" + pv.get("username"));
        map.put("header", headerMap);
        map.put("param",paramMap);
        // @CookieValue String _ga,
        //map.put("cookie", _ga);
        return map;
    }

    @PostMapping("/postCar/{id}/owner/{username}")
    public Map<String,Object> postCar(@PathVariable("id") Integer carId,
                                      @PathVariable("username") String name,
                                      @PathVariable Map<String,String> pv,
                                      @RequestParam("userName") Integer userName,
                                      @RequestParam("email") List<String> email,
                                      @RequestBody String requestBody){
        Map<String,Object> map = new HashMap<>();
        map.put("car1", "新车1-" + carId + "|车主-" + name);
        map.put("car1-copy", "新车1copy-" + pv.get("id") + "|车主-" + pv.get("username"));
        map.put("body", requestBody);
        //请求属性，获取request域中属性，通过用于页面转发时保存数据
        return map;
    }

    // /cars/sell;low=34;brand=byd,audi,yd
    //SpringBoot 默认禁用了矩阵变量注解功能
    // 手动开启: SpringBoot mvc配置中对于url请求路径的处理，UrlPathHelper进行解析
    // removeSemicolonContent 配置对于url路径中 ; 分号后内容移除，故矩阵变量注解不生效
    // 矩阵变量必须用url 路径变量才能生效
    @GetMapping("/matrixCar/{path}")
    public Map<String,Object> matrixCar(@MatrixVariable("low") Integer low,
                                        @MatrixVariable("brand") List<String> brands,
                                        @PathVariable("path") String path){
        Map<String,Object> map = new HashMap<>();
        map.put("low", low);
        map.put("brand", brands);
        map.put("path", path);
        return map;
    }


}
