package com.example.chatt;

import com.example.chatt.dao.UserDao;
import com.example.chatt.pojo.JwtUser;
import com.example.chatt.pojo.User;
import com.example.chatt.service.UserService;
import com.example.chatt.utils.RedisCache;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.annotation.Resource;
import java.util.Date;

@SpringBootTest
class ChattApplicationTests {
    @Resource
    private UserService userService;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private UserDao userDao;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Test
    void contextLoads() {

    }

}
