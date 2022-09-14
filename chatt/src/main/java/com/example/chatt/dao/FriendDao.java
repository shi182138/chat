package com.example.chatt.dao;


import com.example.chatt.pojo.User_Friend;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FriendDao extends MongoRepository<User_Friend, ObjectId> {
}
