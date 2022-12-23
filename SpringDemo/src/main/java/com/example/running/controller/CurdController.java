package com.example.running.controller;

import com.example.running.bean.Business;
import com.example.running.service.CurdService;
import com.example.running.service.mapper.CarBusniessMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

}
