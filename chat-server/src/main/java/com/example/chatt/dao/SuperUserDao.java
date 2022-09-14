package com.example.chatt.dao;

import com.example.chatt.pojo.SuperUser;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SuperUserDao extends MongoRepository<SuperUser, ObjectId> {
}
