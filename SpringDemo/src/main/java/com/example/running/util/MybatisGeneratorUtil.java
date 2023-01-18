package com.example.running.util;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.config.xml.MyBatisGeneratorConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.springframework.data.relational.core.mapping.NamingStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MybatisGeneratorUtil {

    public void generator() throws Exception{
        List<String> warnings = new ArrayList<String>();
        boolean overwrite = true;
        File configFile = new File("classpath:mybatis/generatorConfig.xml");
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(configFile);
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);
    }

    public void generator2() throws Exception{
        List<String> warnings = new ArrayList<String>();
        boolean overwrite = true;
        Configuration config = new Configuration();
        //   ... fill out the config object as appropriate...


        //MyBatisGeneratorConfigurationParser

        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);
    }

    public void generator3(){
        FastAutoGenerator.create("url", "username", "password")
                .globalConfig(builder -> {
                    builder.author("baomidou") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .fileOverride() // 覆盖已生成文件
                            .outputDir("D://"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.baomidou.mybatisplus.samples.generator") // 设置父包名
                            .moduleName("system") // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, "D://")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("t_simple") // 设置需要生成的表名
                            .addTablePrefix("t_", "c_"); // 设置过滤表前缀
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();

    }

    public static void main(String[] args) throws Exception {


    }

    /**
     * @description: 加德士数据库实体生成
     * @author: chenmeng
     * @date: 2022/02/19 11:33
     **/
    public static void caltexGenerate() {
        String author = "chenmeng";
        String jdbcUrl = "jdbc:jtds:sqlserver://192.168.22.57:1433;DatabaseName=RetailOne_Client_GZ_017_v2:currentSchema=dbo";
        String username = "sa";
        String password = "123456";

        String outPutDir = "D:\\Generator\\";
        String pkgName = "com.generer";
        String[] tableNames = {"LoyaltyPointRedemptionLogMaster"};



        /*MPGenerator mpGenerator = new MPGenerator(author, jdbcUrl, username, password, tableNames, outPutDir, pkgName);
        mpGenerator.setNamingStrategy(NamingStrategy.no_change);
        mpGenerator.generate();*/
    }
}
