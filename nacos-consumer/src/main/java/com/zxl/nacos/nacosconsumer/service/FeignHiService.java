package com.zxl.nacos.nacosconsumer.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "nacos-provider")
public interface FeignHiService {

    @GetMapping("/hi")
    public String sayHi(@RequestParam(value = "name",required = false) String name);
}
