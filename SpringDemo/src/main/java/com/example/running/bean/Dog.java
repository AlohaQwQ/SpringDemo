package com.example.running.bean;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author hongyuan
 * @since 2022/7/26 17:40
 * @ConfigurationProperties 配置属性，指定别名为 prefix 自动注入配置好的属性
 **/
//@Component
@ConfigurationProperties(prefix = "mydog")
public class Dog {

    private String walk;

    private String name;

    public String getWalk() {
        return walk;
    }

    public void setWalk(String walk) {
        this.walk = walk;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
