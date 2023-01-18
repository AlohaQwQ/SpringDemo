package com.example.running.service;

import com.example.running.bean.Business;
import com.example.running.bean.City;
import com.example.running.bean.User;
import com.example.running.service.mapper.CarBusniessMapper;
import com.example.running.service.mapper.CityMapper;
import com.example.running.service.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author hongyuan
 * @since 2022/8/9 20:47
 * RestFul
 **/
@Service("CurdService")
public class CurdService {

    Logger logger = LoggerFactory.getLogger(CurdService.class);

    @Autowired
    CarBusniessMapper carBusniessMapper;

    @Autowired
    CityMapper cityMapper;

    @Resource
    UserMapper userMapper;


    public List<Business> selectCarBusiness(Long id){
        return carBusniessMapper.selectAll(id);
    }

    public City getCityById(Long id){
        return cityMapper.getCityById(id);
    }

    public void insertCity(City city){
        cityMapper.insertCity(city);
    }

    public List<User> selectUser(){
        return userMapper.selectList(null);
    }

}
