package com.example.running.service;

import com.example.running.bean.Business;
import com.example.running.service.mapper.CarBusniessMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

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

    public List<Business> selectCarBusiness(Long id){
        return carBusniessMapper.selectAll(id);
    }

}
