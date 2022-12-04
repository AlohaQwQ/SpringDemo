package com.example.running.annotations;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author Aloha
 * @date 2022/11/30 11:52
 * @description
 *         姓名:<input name="userName" value="zhangsan"/><br/>
 *         年龄:<input name="age" value="18"/><br/>
 *         生日:<input name="birth" value="2022/12/10"/><br/>
 *         宠物姓名: <input name="pet.name" value="阿猫"/><br/>
 *         宠物年龄: <input name="pet.age" value="5"/>
 */
public class Person {

    String userName;

    Integer age;

    String weight;

    Date birth;

    Pet pet;

    public Person() {
    }

    public Person(String userName, Integer age) {
        this.userName = userName;
        this.age = age;
    }

    public Person(String userName, Integer age, String weight, Date birth, Pet pet) {
        this.userName = userName;
        this.age = age;
        this.weight = weight;
        this.birth = birth;
        this.pet = pet;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    @Override
    public String toString() {
        return "Person{" +
                "userName='" + userName + '\'' +
                ", age=" + age +
                ", weight='" + weight + '\'' +
                ", birth=" + birth +
                ", pet=" + pet +
                '}';
    }
}
