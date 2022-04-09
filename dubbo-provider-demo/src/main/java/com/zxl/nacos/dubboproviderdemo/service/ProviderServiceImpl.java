package com.zxl.nacos.dubboproviderdemo.service;

public class ProviderServiceImpl implements ProviderService {
    @Override
    public String SayHello(String word) {
        return word;
    }
}
