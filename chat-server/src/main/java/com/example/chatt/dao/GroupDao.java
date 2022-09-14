package com.example.chatt.dao;


import com.example.chatt.pojo.Group;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GroupDao extends MongoRepository<Group, ObjectId> {
}
