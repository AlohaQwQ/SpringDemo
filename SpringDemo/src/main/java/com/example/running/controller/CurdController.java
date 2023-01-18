package com.example.running.controller;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.example.running.bean.Business;
import com.example.running.bean.City;
import com.example.running.bean.User;
import com.example.running.service.CurdService;
import com.example.running.service.UserService;
import com.example.running.service.mapper.CarBusniessMapper;
import com.example.running.service.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * @author hongyuan
 * @since 2022/8/9 20:47
 * RestFul
 **/
@Controller
public class CurdController {

    Logger logger = LoggerFactory.getLogger(CurdController.class);

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    DataSource dataSource;

    @Autowired
    CurdService curdService;

    @Autowired
    UserService userService;

    @PostMapping("/selectSql")
    public List<Map<String, Object>> selectSql(){
        List<Map<String, Object>> list = jdbcTemplate.queryForList("select * from car_busniess");
        logger.info("list:{}", list);
        return list;
    }

    @ResponseBody
    @PostMapping("/selectCarBusiness")
    public List<Business> selectCarBusiness(@RequestParam Long id){
        List<Business> list = curdService.selectCarBusiness(id);
        logger.info("list:{}", list);
        return list;
    }

    @ResponseBody
    @PostMapping("/getCityById")
    public City getCityById(@RequestParam Long id){
        City city = curdService.getCityById(id);
        logger.info("city:{}", city);
        return city;
    }

    @ResponseBody
    @PostMapping("/insertCity")
    public City insertCity(City city){
        curdService.insertCity(city);
        logger.info("insertCity:{}", true);
        return city;
    }

    @ResponseBody
    @PostMapping("/selectUser")
    public List<User> selectUser(){
        List<User> userList1 = curdService.selectUser();

        //使用mybatisplus 查询数据库
        List<User> userList = userService.list();
        logger.info("userList:{}", userList);
        return userList;
    }

    @Resource
    UserMapper userMapper;

    @ResponseBody
    @PostMapping("/deleteUser")
    public String deleteUser(@RequestParam String userId){
        //使用mybatisplus
        User user = userService.getById(userId);


        //Service Chain用法
        // 链式查询 普通
        //QueryChainWrapper<T> query();
        // 链式查询 lambda 式。注意：不支持 Kotlin
        //LambdaQueryChainWrapper<T> lambdaQuery();
        // 示例：
        //query().eq("column", value).one();
        //lambdaQuery().eq(Entity::getId, value).list();


        User user1 = userService.lambdaQuery().eq(User::getId, userId).one();
        User user2 = userService.query().eq("id", userId).one();


        //boolean result = userService.removeById(user2);
        //logger.info("deleteUser:{}", result);


        User user3 = new LambdaQueryChainWrapper<>(userMapper).eq(User::getId, userId).one();

        return "success";
    }


}
