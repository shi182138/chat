package com.example.chatt.service.impl;

import com.example.chatt.dao.ValidateMessageDao;
import com.example.chatt.pojo.SimpleGroup;
import com.example.chatt.pojo.ValidateMessage;
import com.example.chatt.pojo.ValidateMessageResultVo;
import com.example.chatt.pojo.vo.ValidateMessageResponseVo;
import com.example.chatt.service.ValidateMessageService;
import com.mongodb.client.result.UpdateResult;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ValidateMessageServiceImpl implements ValidateMessageService {
    @Autowired
    private ValidateMessageDao validateMessageDao;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public ValidateMessage findValidateMessage(String roomId, Integer status, Integer validateType) {
        System.out.println(roomId);
        System.out.println(status);
        System.out.println(validateType);
        return validateMessageDao.findValidateMessageByRoomIdAndStatusAndValidateType(roomId, status, validateType);
    }

    @Override
    public List<ValidateMessageResponseVo> getMyValidateMessageList(String userId) {
//        System.out.println("---------------------------------------->" + userId);
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.lookup(
                        "groups",
                        "groupId",
                        "_id",
                        "groupList"
                ),
                  Aggregation.match(Criteria.where("receiverId").is(new ObjectId(userId)))
        );
        List<ValidateMessageResultVo> validateMessages = mongoTemplate.aggregate(aggregation,"validateMessages",ValidateMessageResultVo.class).getMappedResults();
//        System.out.println("validateMessageService--------------->" + validateMessages);
        List<ValidateMessageResponseVo> responseVoList = new ArrayList<>();
        ValidateMessageResponseVo item;
        for (ValidateMessageResultVo validateMessage : validateMessages) {
            item = new ValidateMessageResponseVo();
            BeanUtils.copyProperties(validateMessage, item);
            if (validateMessage.getGroupId() != null && validateMessage.getGroupList() != null && validateMessage.getGroupList().size() != 0) {
                item.setGroupInfo(new SimpleGroup());
                item.getGroupInfo().setGroupId(validateMessage.getGroupList().get(0).getGroupId().toString());
                item.getGroupInfo().setTitle(validateMessage.getGroupList().get(0).getTitle());
            }
            responseVoList.add(item);
        }
        return responseVoList;
    }

    @Override
    public void changeFriendValidateNewsStatus(String validateMessageId, Integer status) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(validateMessageId)));
        Update update = new Update();
        update.set("status",status);
        UpdateResult result = mongoTemplate.upsert(query, update, "validateMessages");
    }

    @Override
    public ValidateMessage addValidateMessage(ValidateMessage validateMessage) {
//        System.out.println("-----------------------------validateMessage");
//        System.out.println(validateMessage);
        ValidateMessage result = findValidateMessage(validateMessage.getRoomId(),0,validateMessage.getValidateType());
        if (result == null) {
            return validateMessageDao.save(validateMessage);
        }
        return null;
    }

}
