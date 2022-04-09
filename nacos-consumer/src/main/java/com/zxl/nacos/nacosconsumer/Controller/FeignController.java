package com.zxl.nacos.nacosconsumer.Controller;

import com.zxl.nacos.nacosconsumer.service.FeignHiServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FeignController {
    @Autowired
    private FeignHiServiceImpl feignHiService;

    @GetMapping("/hi")
    public String sayHi(@RequestParam(value = "name",required = false) String name){
        return feignHiService.sayHi(name);
    }

}
