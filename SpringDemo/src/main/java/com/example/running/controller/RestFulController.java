package com.example.running.controller;

import org.springframework.web.bind.annotation.*;

/**
 * @author hongyuan
 * @since 2022/8/9 20:47
 * RestFul
 **/
@RestController
public class RestFulController {

    @GetMapping("/user")
    public String getUser(){

        return "";
    }

    @PostMapping("/user")
    public String postUser(){

        return "";
    }

    @PutMapping("/user")
    public String putUser(){

        return "";
    }

    @DeleteMapping("/user")
    public String deleteUser(){

        return "";
    }
}
