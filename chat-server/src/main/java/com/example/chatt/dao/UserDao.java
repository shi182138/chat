package com.example.chatt.dao;

import com.example.chatt.pojo.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoAdminOperations;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserDao extends MongoRepository<User, ObjectId> {
    User findUserByUsernameOrCode(String username,String code);
    User findUserByUsername(String username);
//    User findUsersByUserId(String userId);
    User findUserByUserId(String userId);
}
