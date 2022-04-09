package com.zxl.nacos.mongodbdemo.config.autoIncKey.demo;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface IDIncrDemoRepository extends MongoRepository<IDIncrDemo,Long> {
}
