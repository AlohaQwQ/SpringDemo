package com.example.running.controller;

import com.example.running.annotations.Person;
import com.example.running.bean.Dog;
import com.example.running.bean.Zhouzhou;
import com.example.running.util.FeignApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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
public class FeignController {


    @Autowired
    private FeignApi feignApi;

    @GetMapping("/testFeign")
    public String getUserById(@RequestParam String hello) {
        return feignApi.hello(hello);
    }

}
