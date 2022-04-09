package com.zxl.nacos.shardingspheredemo.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zxl.nacos.shardingspheredemo.ShardingsphereDemoApplication;
import com.zxl.nacos.shardingspheredemo.entity.Course;
import com.zxl.nacos.shardingspheredemo.entity.Dict;
import com.zxl.nacos.shardingspheredemo.entity.User;
import org.apache.shardingsphere.api.hint.HintManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ShardingsphereDemoApplication.class)
public class StardardMapperTest {
    @Autowired
    CourseMapper courseMapper;
    @Autowired
    DictMapper dictMapper;
    @Autowired
    UserMapper userMapper;

    @Test
    public void addCourse(){
//        for(int i = 0 ; i < 10 ; i ++){
//            Course c = new Course();
////            c.setCid(Long.valueOf(i));
//            c.setCname("shardingsphere");
//            c.setUserId(Long.valueOf(""+(1000+i)));
//            c.setCstatus("1");
//            courseMapper.insert(c);
//        }

        Course c = new Course();
//            c.setCid(Long.valueOf(i));
        c.setCname("shardingsphere");
        c.setUserId(Long.valueOf(""+(1000+11)));
        c.setCstatus("1");
        courseMapper.insert(c);

    }

    @Test
    public void queryCourse(){
        // select * from course where cid = ? or cid in(?,?)
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cid",553953707188813824L);
        queryWrapper.orderByDesc("cid");
//        queryWrapper.in()

        List<Course> courses = courseMapper.selectList(queryWrapper);
        courses.forEach(System.out::println);
    }

    @Test
    public void queryOrderRange(){
        //select * from course where cid between ? and ?
        QueryWrapper<Course> wrapper = new QueryWrapper<>();
        wrapper.between("cid",553953308109176833L,553953308109176833L);

        List<Course> courses = courseMapper.selectList(wrapper);
        courses.forEach(course -> System.out.println(course));
    }

    @Test
    public void queryCourseComplex(){
        // SELECT  cid,cname,user_id,cstatus  FROM  course  WHERE  cid BETWEEN ? AND ? AND user_id = ?
        QueryWrapper<Course> wrapper = new QueryWrapper<>();
        wrapper.between("cid",553953308109176833L,553953308109176833L);
        wrapper.eq("user_id",1011L);

        List<Course> courses = courseMapper.selectList(wrapper);
        courses.forEach(course -> System.out.println(course));
    }


    @Test
    public void queryCourseByHint(){
        // SELECT  cid,cname,user_id,cstatus  FROM course_2
        HintManager hintManager = HintManager.getInstance();
        hintManager.addTableShardingValue("course",2);
        List<Course> courses = courseMapper.selectList(null);
        courses.forEach(course -> System.out.println(course));
        hintManager.close();
    }




// --------------- 绑定表示例 -------------------------------------------
    @Test
    public void addDict(){
        Dict d1 = new Dict();
        d1.setUstatus("1");
        d1.setUvalue("正常");
        dictMapper.insert(d1);

        Dict d2 = new Dict();
        d2.setUstatus("0");
        d2.setUvalue("不正常");
        dictMapper.insert(d2);

        for(int i = 0 ; i < 10 ; i ++){
            User user = new User();
            user.setUsername("user No "+i);
            user.setUstatus(""+(i%2));
            user.setUage(i*10);
            userMapper.insert(user);
        }
    }

    @Test
    public void queryUserStatus(){
        List<User> users = userMapper.queryUserStatus();
        users.forEach(user -> System.out.println(user));
    }


    // --------------- 主从 -------------------------------------------
    @Test
    public void addDictByMS(){
        Dict d1 = new Dict();
        d1.setUstatus("1");
        d1.setUvalue("正常");
        dictMapper.insert(d1);

        Dict d2 = new Dict();
        d2.setUstatus("0");
        d2.setUvalue("不正常");
        dictMapper.insert(d2);
    }

    @Test
    public void queryDictByMS(){
        List<Dict> dicts = dictMapper.selectList(null);
        dicts.forEach(dict -> System.out.println(dict));
    }

}
