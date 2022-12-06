package com.example.running.controller;

import com.example.running.annotations.Person;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
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

    /**
     * @author Aloha
     * @date 2022/12/5 12:06
     * @description
     *
     * 1.浏览器发请求直接返回xml [application/xml]  jacksonXmlConverter
     * 2.如果是ajax请求则返回json [application/json]  jacksonJsonConverter
     * 3.如果是 硅谷App请求，则返回自定义协议数据  [application/x-guigu]  xxxConverter
     *
     * 步骤：
     * 1.添加自定义 MessageConverter 进入系统内
     * 2.系统解析请求时就会统计出所有 MessageConverter 能操作哪些类型
     * 3.根据内容协商规则，客户端指定接收 [application/x-guigu] 类型数据 (属性值1;属性值2;)，并且具备该数据类型解析能力 xxxConverter
     */
    @PostMapping("/getPersonForNs")
    public Person getPersonForNs(@RequestParam String personName, @RequestParam Integer personAge, @RequestParam Date personBirth){
        Map<String,Object> map = new HashMap<>();
        Person person = new Person(personName, personAge, "110kg", personBirth, null);
        return person;
    }
}
