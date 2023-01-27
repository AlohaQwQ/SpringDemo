package com.example.running;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.data.redis.core.ValueOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@SpringBootTest(classes = MainApplication.class)
class MainApplicationTests {

    static Logger logger = LoggerFactory.getLogger(MainApplicationTests.class);

    @Autowired
    JdbcTemplate jdbcTemplate;

    //成员变量注入
    //@Autowired
    //@Resource
    DataSource dataSource;

    //@Autowired
    //StringRedisTemplate stringRedisTemplate;

    //构造方法注入
    //@Autowired
    /*public MainApplicationTests(DataSource dataSource) {
        this.dataSource = dataSource;
    }*/

    //禁用该测试方法
    @Disabled
    @Test
    void contextLoads() throws SQLException {
        //List<Map<String, Object>> list = jdbcTemplate.queryForList("select * from car_busniess");
        //logger.info("list:{}", list);


        logger.info("dataSource.getClass():{}", dataSource.getClass());

        Connection connection = dataSource.getConnection();
        connection.getCatalog();
    }


    //方法参数注入
    @Resource
    public void setService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 测试Test前置条件
     */
    @Test
    void testAssumptions(){
        //当条件不成立时跳出
        Assumptions.assumeTrue(false);
        logger.info("assertAll:{}", "assertAll");
    }

    @DisplayName("heihei")
    @Transactional
    @Test
    void testRedis() {
        logger.info("testRedis:{}", 1);
        //k-v形式存储数据
        /*ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        valueOperations.set("redisTest3", "11");
        valueOperations.set("redisTest4", "22");
        String redisTest1 = valueOperations.get("redisTest1");
        logger.info("redisTest1:{}", redisTest1);*/
    }


    /**
     * 断言2个值相等
     */
    @Test
    void testAssertEquals(){
        int result = add(3,3);
        Assertions.assertEquals(6, result, "终于错了");
        logger.info("testAssertEquals:{}", result);
    }

    int add(int a, int b){
        return a+b;
    }

    /**
     * 断言所有逻辑
     */
    @Test
    void assertAll(){
        Assertions.assertAll("AssertAll Error",
                () -> Assertions.assertTrue(true),
                () -> Assertions.assertEquals("heihei", "heihei", "不相同！"));
        logger.info("assertAll:{}", "assertAll");
    }

    /**
     * 异常断言
     */
    @Test
    void assertThrows(){
        //Assertions.fail("马上失败");
        Assertions.assertThrows(Throwable.class,
                () -> Assertions.assertTrue(false), "居然正常");
        logger.info("assertThrows:{}", "assertThrows");
    }


    //重复执行测试方法
    @RepeatedTest(10)
    void testRepeatedTest() {
        logger.info("test:{}", "testRepeatedTest");
    }

    @BeforeEach
    void testBeforeEach() {
        logger.info("test:{}", "testBeforeEach");
    }

    @BeforeAll
    static void testBeforeAll() {
        logger.info("test:{}", "testBeforeAll");
    }

    @AfterEach
    void testAfterEach() {
        logger.info("test:{}", "testAfterEach");
    }

    @AfterAll
    static void testAfterAll() {
        logger.info("test:{}", "testAfterAll");
    }
}
