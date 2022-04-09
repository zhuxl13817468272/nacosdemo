package com.zxl.nacos.mongodbdemo.juniorDao;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.sun.org.apache.regexp.internal.RE;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import com.zxl.nacos.mongodbdemo.entity.UserDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 *  使用4  MongodbTemplate   其他JPA的三种方法参见接口UserRepository.class
 */
@Repository
public class UserDao {
    @Autowired
    private MongoTemplate mongoTemplate;

    public void insert(UserDO user){
        UserDO insert = mongoTemplate.insert(user);
        System.out.println("插入成功："+insert.toString());
    }

    public UserDO findById(Integer id){
        UserDO userDO = mongoTemplate.findById(id, UserDO.class);
        System.out.println("根据id查询1成功："+userDO.toString());
        return userDO;
    }

    public List<UserDO> findAllById(List<Integer> ids) {
        List<UserDO> list = mongoTemplate.find(new Query(Criteria.where("_id").in(ids)), UserDO.class);
        list.stream().forEach(System.out::println);
        return list;
    }

    public UserDO findByUsername(String username) {
        UserDO userDO = mongoTemplate.findOne(new Query(Criteria.where("username").is(username)), UserDO.class);
        System.out.println("查询成功："+userDO);
        return userDO;
    }

    public void deleteById(Integer id) {
        DeleteResult remove = mongoTemplate.remove(new Query(Criteria.where("_id").is(id)), UserDO.class);
        System.out.println("删除成功："+remove.toString());
    }

    public void updateById(UserDO entity){
        final Update update = new Update(); //生成update对象
        // 反射遍历 entity 对象，将非空字段设置到 Update 中
        ReflectionUtils.doWithFields(entity.getClass(), new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                if("id".equals(field.getName()) // 排除id字段，因为作为查询主键
                || field.getAnnotation(Transient.class) != null //排除@Transient注解的字段，因为字段不和数据库有映射关系
                || Modifier.isStatic(field.getModifiers())){ //排除静态字段
                    return;
                }
                // 设置字段可反射
                if( !field.isAccessible()){
                    field.setAccessible(true);
                }
                // 排除字段为空的情况
                if(field.get(entity) == null){
                    return;
                }
                Update set = update.set(field.getName(), field.get(entity));
                System.out.println("update设置成功："+set.toString());
            }
        });
        //防御，避免有业务传递空的Update对象
        if(update.getUpdateObject().isEmpty()){
            return;
        }

        UpdateResult result = mongoTemplate.updateFirst(new Query(Criteria.where("_id").is(entity.getId())), update, UserDO.class);
        System.out.println("更新成功："+result.toString());
    }

}
