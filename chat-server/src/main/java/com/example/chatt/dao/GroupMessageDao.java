package com.example.chatt.dao;


import com.example.chatt.pojo.GroupMessage;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GroupMessageDao extends MongoRepository<GroupMessage, ObjectId> {
}
