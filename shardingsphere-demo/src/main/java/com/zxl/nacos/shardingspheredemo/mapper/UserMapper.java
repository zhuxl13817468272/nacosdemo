package com.zxl.nacos.shardingspheredemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zxl.nacos.shardingspheredemo.entity.User;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author ：楼兰
 * @date ：Created in 2021/1/5
 * @description:
 **/
public interface UserMapper extends BaseMapper<User> {

    @Select("select u.user_id,u.username,u.uage,d.uvalue ustatus from t_user u left join t_dict d on u.ustatus = d.ustatus")
    public List<User> queryUserStatus();
}
