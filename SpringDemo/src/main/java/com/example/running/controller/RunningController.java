package com.example.running.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author Aloha
 * @date 2022/7/24 17:55
 * @description 控制器
 */
@RestController
public class RunningController {

    @RequestMapping("/hello")
    public String hello(@RequestParam String hello){
        return "hello:" + hello;
    }
}
