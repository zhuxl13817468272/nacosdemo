package com.zxl.nacos.nacosprovider.comtroller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class ConfigController {
    @Value("${username:lily}")
    String username;

    @GetMapping("/username")
    public String get(){
        return username;
    }
}
