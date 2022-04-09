package com.zxl.nacos.nacosconsumer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeignHiServiceImpl {
    @Autowired
    private FeignHiService feignHiService;

    public String sayHi(String name ){
        return feignHiService.sayHi(name);
    }

}
