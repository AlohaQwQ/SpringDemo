package com.example.running;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@SpringBootTest(classes = MainApplication.class)
class MainApplicationTests {

    Logger logger = LoggerFactory.getLogger(MainApplicationTests.class);

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    DataSource dataSource;

    @Test
    void contextLoads() throws SQLException {
        List<Map<String, Object>> list = jdbcTemplate.queryForList("select * from car_busniess");
        logger.info("list:{}", list);


        logger.info("dataSource.getClass():{}", dataSource.getClass());

        Connection connection = dataSource.getConnection();
        connection.getCatalog();
    }

}
