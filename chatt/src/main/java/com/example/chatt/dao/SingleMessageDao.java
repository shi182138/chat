package com.example.chatt.dao;

import com.example.chatt.pojo.SingleMessage;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SingleMessageDao extends MongoRepository<SingleMessage, ObjectId>{
}
