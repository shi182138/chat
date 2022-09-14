package com.example.chatt.dao;

import com.example.chatt.pojo.ValidateMessage;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ValidateMessageDao extends MongoRepository<ValidateMessage, ObjectId> {
    ValidateMessage findValidateMessageByRoomIdAndStatusAndValidateType(String roomId, Integer status, Integer validateType );
}
