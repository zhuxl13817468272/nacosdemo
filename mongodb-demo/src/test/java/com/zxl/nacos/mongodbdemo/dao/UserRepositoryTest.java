package com.zxl.nacos.mongodbdemo.dao;

import com.zxl.nacos.mongodbdemo.MongodbDemoApplication;
import com.zxl.nacos.mongodbdemo.config.autoIncKey.demo.IDIncrDemo;
import com.zxl.nacos.mongodbdemo.config.autoIncKey.demo.IDIncrDemoRepository;
import com.zxl.nacos.mongodbdemo.entity.UserDO;
import com.zxl.nacos.mongodbdemo.juniorDao.UserDao;
import com.zxl.nacos.mongodbdemo.juniorDao.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MongodbDemoApplication.class)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserDao userDao;
    @Autowired
    private IDIncrDemoRepository idIncrDemoRepository;

    @Test
    public void testInsert(){
        for(int i =0;i<=8;i++){
            UserDO user = new UserDO();
            user.setId(i);
            user.setUsername("artz"+i);
            user.setPassword("zxl"+i);
            user.setCreateTime(new Date());
            userRepository.insert(user);
        }
    }

    @Test
    public void testUpdate(){
        Optional<UserDO> optionalUserDO = userRepository.findById(1);
        Assert.isTrue(optionalUserDO.isPresent(),"用户必须存在，该用户不存在");

        UserDO userDO = optionalUserDO.get();
        userDO.setUsername("zhuxl");
        userRepository.save(userDO); // 使用 save 方法来更新的话，必须是全量字段，否则其它字段会被覆盖。
    }

    @Test
    public void testDelete(){
        userRepository.deleteById(1);
    }

    @Test
    public void testSelectById(){
        //查询一条记录
        Optional<UserDO> optionalUserDO = userRepository.findById(1);
        Assert.isTrue(optionalUserDO.isPresent(),"用户不存在");
        System.out.println(optionalUserDO.get());

        // 查询多条记录
        Iterable<UserDO> userDOIterable = userRepository.findAllById(Arrays.asList(1, 2));
        userDOIterable.forEach(System.out::println);
    }



    // -----------------------------------------Spring data 基于方法名查询实例 ----------------------------------------------------
    @Test
    public void testFindByName(){
        UserDO artz2 = userRepository.findByUsername("artz2");
        System.out.println(artz2.toString());
    }

    @Test
    public void testFindByNameLike(){
        Sort sort = new Sort(Sort.Direction.DESC, "id"); // 排序
        PageRequest pageable = PageRequest.of(0, 5, sort); //分页

        Page<UserDO> page = userRepository.findByUsernameLike("art", pageable);
        System.out.println("查询出的总记录数量：" + page.getTotalElements() + " ，筛选出按照pageable的记录" + page.getContent());
    }

    // -----------------------------------------Spring data example 查询实例 ----------------------------------------------------
    @Test
    public void testFindByUsername01() {
        UserDO user = userRepository.findByUsername01("artz");
        System.out.println(user);
    }

    @Test
    public void testFindByUsernameLike01() {
        UserDO user = userRepository.findByUsernameLike01("artz6");
        System.out.println(user);
    }

    // ----------------------------------------------mongoTemplate 使用实例 ----------------------------------------------------
    @Test // 插入一条记录
    public void testInsertMongoTemplate() {
        // 创建 UserDO 对象
        UserDO user = new UserDO();
        user.setId(7); // 这里先临时写死一个 ID 编号，后面演示自增 ID 的时候，在修改这块
        user.setUsername("art7");
        user.setPassword("zxl7");
        user.setCreateTime(new Date());

        userDao.insert(user);
    }

    // 这里要注意，如果使用 save 方法来更新的话，必须是全量字段，否则其它字段会被覆盖。
    @Test // 更新一条记录
    public void testUpdateMongoTemplate() {
        // 创建 UserDO 对象
        UserDO updateUser = new UserDO();
        updateUser.setId(7);
        updateUser.setUsername("nicai");

        // 执行更新
        userDao.updateById(updateUser);
    }

    @Test // 根据 ID 编号，查询一条记录
    public void testSelectByIdMongoTemplate() {
        UserDO userDO = userDao.findById(7);
        System.out.println(userDO);
    }

    @Test
    public void findByUsername() {
        userDao.findByUsername("artz");
    }

    @Test // 根据 ID 编号数组，查询多条记录
    public void testSelectByIdsMongoTemplate() {
        List<UserDO> users = userDao.findAllById(Arrays.asList(1, 2));
    }

    @Test // 根据 ID 编号，删除一条记录
    public void testDeleteMongoTemplate() {
        userDao.deleteById(7);
    }


    @Test
    public void test(){
        idIncrDemoRepository.deleteAll();

        IDIncrDemo user1 = new IDIncrDemo("Helen", 18);
        IDIncrDemo user2 = new IDIncrDemo("Steven", 18);
        idIncrDemoRepository.save(user1);
        idIncrDemoRepository.save(user2);
        System.out.println("Helen的id: "+user1.getId());
        System.out.println("Steven的id: "+user2.getId());
    }


}