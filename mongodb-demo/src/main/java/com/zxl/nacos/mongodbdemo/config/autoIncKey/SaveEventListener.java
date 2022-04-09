package com.zxl.nacos.mongodbdemo.config.autoIncKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 *  在保存对象时，通过反射方式为其生成ID。参考网址：https://blog.csdn.net/try_try_try/article/details/80612666
 *  使用方式：在entity类的id自增字段上加@AutoIncKey注解
 */
@Component
public class SaveEventListener extends AbstractMongoEventListener<Object> {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Object> event) {
        final Object source = event.getSource();
        if(source != null){
            ReflectionUtils.doWithFields(source.getClass(), new ReflectionUtils.FieldCallback() {
                @Override
                public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                    ReflectionUtils.makeAccessible(field); // 将一个字段设置为可读写，主要针对private字段
                    // 如果字段添加了我们自定义的AutoValue注解
                    if(field.isAnnotationPresent(AutoIncKey.class)
                    && field.get(source) instanceof Number
                    && field.getLong(source) == 0){
                        //设置自增ID
                        field.set(source,getNextAutoId(source.getClass().getSimpleName()));
                    }
                }
            });
        }
    }

    //获取下一个自增ID
    private Long getNextAutoId(String collName) {
        Query query = new Query(Criteria.where("collName").is(collName));
        Update update = new Update();
        update.inc("seqId",1);

        FindAndModifyOptions modifyOptions = new FindAndModifyOptions();
        modifyOptions.upsert(true);
        modifyOptions.returnNew(true);
        SeqInfo seqInfo = mongoTemplate.findAndModify(query, update, modifyOptions, SeqInfo.class);
        return seqInfo.getSeqId();
    }

}
