package com.example.running.controller;

import com.example.running.bean.Dog;
import com.example.running.bean.Zhouzhou;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author Aloha
 * @date 2022/7/24 17:55
 * @description 控制器
 */
@RestController
public class RunningController {

    @Resource
    public Zhouzhou zhouzhou;

    @Autowired
    private Dog dog;

    @RequestMapping("/hello")
    public String hello(@RequestParam String hello){
        zhouzhou.getCat();
        return "hello:" + hello;
    }

    @RequestMapping("/dog")
    public Dog dog(){
       return dog;
    }
}
