package com.example.chatt.dao;

import com.example.chatt.pojo.SystemUser;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SysDao extends MongoRepository<SystemUser, ObjectId> {
}
