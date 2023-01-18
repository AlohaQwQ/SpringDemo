package com.example.running.service.mapper;

import com.example.running.bean.Business;
import com.example.running.bean.City;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author Aloha
 * @date 2022/12/24 16:28
 * @description 使用注解 + mapper 文件 的方式使用mybatis
 */
//@Mapper
public interface CityMapper {

    @Select("select * from city where id=#{id}")
    City getCityById(@Param("id") Long id);

    // useGeneratedKeys = true, keyProperty = "id"  使用自增主键，值为id
/*    @Insert("INSERT INTO city (name, state, country) VALUES(#{name}, #{state}, #{country})")
    @Options(useGeneratedKeys = true, keyProperty = "id")*/
    void insertCity(City city);

    @Select("select * from city")
    List<City> getAll();

}
