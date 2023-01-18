package com.example.running.service.mapper;

import com.example.running.bean.Business;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

//@Mapper
public interface CarBusniessMapper {
    List<Business> selectAll(@Param("id") Long id);
}
