package com.zxl.nacos.nacosconsumer.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class RobbinController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("hi-resttemplate")
    public String robbinHi(){
        return restTemplate.getForObject("http://nacos-provider/hi?name=resttemplate",String.class);
    }
}
