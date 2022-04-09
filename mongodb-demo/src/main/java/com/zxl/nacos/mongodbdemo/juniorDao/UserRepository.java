package com.zxl.nacos.mongodbdemo.juniorDao;

import com.zxl.nacos.mongodbdemo.entity.UserDO;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;


public interface UserRepository extends MongoRepository<UserDO,Integer> {

    /**
     *  使用1  基础CRUD单表继承方法查询
     */

    /**
     *  使用2.1  基于方法名查询
     *         在Spring Data中，支持根据方法名(使用API)自动生成对应的查询（where）条件,具体方法名以findBy\existsBy\countBy\deleteBy开头
     *         具体详细参考：https://docs.spring.io/spring-data/mongodb/docs/3.1.2/reference/html/#repositories
     *                      https://docs.spring.io/spring-data/jpa/docs/2.2.0.RELEASE/reference/html/#jpa.query-methods.query-creation
     */
    UserDO findByUsername(String username);

    Page<UserDO> findByUsernameLike(String username, Pageable pageable);

    UserDO findByUsernameAndPassword(String username,String password);
    UserDO findByUsernameOrPassword(String username,String password);
    List<UserDO> findTop5ByUsernameOrderByPasswordAsc(String lastname);



    /**
     *  使用2.2  基于Query查询
     *          具体详细参考：https://docs.spring.io/spring-data/mongodb/docs/3.1.2/reference/html/#mongodb.repositories.queries.sort
     */

    @Query("{$and:[{MessageID:?0},{Time:{$gte:?1}},{Time:{$lte:?2}}]}")
    List<UserDO> getResponseByTime(Integer messageId, Date startTime, Date endTime);
    @Query("{'username': ?#{[0]} }")
    List<UserDO> findByQueryWithExpression(String param0);

    /**
     *  使用3 基于Example查询
     *          实际场景中，我们很少选择基于Spring Data Example查询
     */
    default UserDO findByUsername01(String username){
        UserDO user = new UserDO();
        user.setUsername(username);

        Example<UserDO> userDOExample = Example.of(user);
        UserDO userDO = findOne(userDOExample).orElse(null);
        return userDO;
    }

    default UserDO findByUsernameLike01(String username){
        UserDO user = new UserDO();
        user.setUsername(username);

        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("username", ExampleMatcher.GenericPropertyMatchers.contains());// 模糊匹配 username 查询
        Example<UserDO> userDOExample = Example.of(user, exampleMatcher);
        UserDO userDO = findOne(userDOExample).orElse(null);
        return userDO;
    }

    /**
     *  使用4  MongodbTemplate 参见UserDao.class
     */

}
