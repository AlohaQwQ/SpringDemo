package com.example.running.annotations;

public class Person {

    @TestAnnotations(age = 20,name = "name")
    int age;

    @TestAnnotations(age = 30,name = "ag")
    String name;

    String weight;

    public Person(int age, String name, String weight) {
        this.age = age;
        this.name = name;
        this.weight = weight;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Person{" +
                "age=" + age +
                ", name='" + name + '\'' +
                ", weight='" + weight + '\'' +
                '}';
    }
}
