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
        return "get";
    }

    @PostMapping("/user")
    public String postUser(){
        return "post";
    }

    @PutMapping("/user")
    public String putUser(){
        return "put";
    }

    @DeleteMapping("/user")
    public String deleteUser(){
        return "delete";
    }
}
