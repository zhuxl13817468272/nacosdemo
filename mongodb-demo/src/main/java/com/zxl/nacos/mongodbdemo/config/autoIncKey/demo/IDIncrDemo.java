package com.zxl.nacos.mongodbdemo.config.autoIncKey.demo;

import com.zxl.nacos.mongodbdemo.config.autoIncKey.AutoIncKey;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "idIncr")
public class IDIncrDemo {

    @AutoIncKey
    @Id
    @Field("id")
    public long id;
    @Field("name")
    public String name;
    @Field("age")
    public int age;

    public IDIncrDemo(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "IDIncrDemo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
