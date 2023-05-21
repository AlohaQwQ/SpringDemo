package com.example.running.util;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "feignTest", url = "localhost:8088/")
public interface FeignApi {
    @GetMapping("test/{data}")
    String test1(@PathVariable("data") String data);

    @PostMapping("test/{data}")
    String test2(@PathVariable("data") String data);


    @PostMapping("hello/{data}")
    String hello(@RequestParam("data") String data);

}
