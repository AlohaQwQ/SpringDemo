package com.example.running.bean;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author hongyuan
 * @since 2022/7/26 17:40
 * 配置属性，指定别名为 prefix 自动注入配置好的属性
 **/
@Component
@ConfigurationProperties(prefix = "xiaozhu")
public class Zhouzhou {

    private String name;

    private Cat cat;

    private Dog dog;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Cat getCat() {
        return cat;
    }

    public void setCat(Cat cat) {
        this.cat = cat;
    }

    public Dog getDog() {
        return dog;
    }

    public void setDog(Dog dog) {
        this.dog = dog;
    }
}
