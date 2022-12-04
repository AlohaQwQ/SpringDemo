package com.example.running.controller;

import com.example.running.annotations.Person;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Aloha
 * @date 2022/11/30 11:52
 * @description POJO
 */
@RestController
public class PojoController {

    /**
     * @author Aloha
     * @date 2022/11/30 15:58
     * @description 自定义Bean 参数，数据绑定：页面提交的请求数据(get/post)都可以和对象属性进行绑定
         */
    @PostMapping("/saveUser")
    public Map<String,Object> saveUser(Person person){
        Map<String,Object> map = new HashMap<>();
        map.put("person", person.toString());

        return map;
    }

    /**
     * @author Aloha
     * @date 2022/12/1 18:45
     * @description RequestResponseBodyMethodProcessor 负责处理返回值标了@ResponseBody 注解
     */
    @PostMapping("/getPerson")
    public Person getPerson(@RequestParam Integer personId){
        Map<String,Object> map = new HashMap<>();
        Person person = new Person("name-" + personId, 0);
        return person;
    }
}
