package com.zxl.nacos.shardingspheredemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zxl.nacos.shardingspheredemo.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author ：楼兰
 * @date ：Created in 2021/1/4
 * @description:
 **/
@Mapper
public interface CourseMapper extends BaseMapper<Course> {
}
