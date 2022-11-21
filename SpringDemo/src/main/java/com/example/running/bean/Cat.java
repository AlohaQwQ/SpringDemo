package com.example.running.bean;

import com.example.running.annotations.TestAnnotations;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
@TestAnnotations(name = "this cat class")//注解 修饰类
public class Cat {

    private String eatFood;
    @TestAnnotations(age = 10,name = "cat")
    private String name;

    @TestAnnotations(name = "this cat construtor")
    public Cat(String eatFood, String name) {
        this.eatFood = eatFood;
        this.name = name;
    }

    public String getEatFood() {
        return eatFood;
    }

    public void setEatFood(String eatFood) {
        this.eatFood = eatFood;
    }

    @TestAnnotations(name = "this cat name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Cat{" +
                "eatFood='" + eatFood + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
