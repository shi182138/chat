package com.example.chatt.service.impl;

import com.example.chatt.dao.GroupMessageDao;
import com.example.chatt.dao.GroupUserDao;
import com.example.chatt.pojo.Group;
import com.example.chatt.pojo.GroupMessage;
import com.example.chatt.pojo.GroupUser;
import com.example.chatt.pojo.SimpleUser;
import com.example.chatt.pojo.vo.*;
import com.example.chatt.service.GroupUserService;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

@Service
public class GroupUserServiceImpl implements GroupUserService {
    @Autowired
    private GroupUserDao groupUserDao;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private GroupMessageDao groupMessageDao;


    @Override
    public List<MyGroupResultVo> getGroupUsersByUserId(String userId) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("userId").is(new ObjectId(userId))),
                Aggregation.lookup(
                        "groups",
                        "groupId",
                        "_id",
                        "groupInfo"
                )
        );
        List<MyGroupResultVo> groupResultVos =  mongoTemplate.aggregate(aggregation, "group_users", MyGroupResultVo.class).getMappedResults();
        return groupResultVos;
    }

    @Override
    public List<MyMyGroupResultVo> getRecentGroup(RecentGroupVo recentGroupVo) {
        List<ObjectId> groupIds = new ArrayList<>();
        for (String son : recentGroupVo.getGroupIds()) {
            groupIds.add(new ObjectId(son));
        }
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.lookup(
                        "groups",
                        "groupId",
                        "_id",
                        "groupList"
                ), Aggregation.match(Criteria.where("groupId").in(groupIds).and("userId").is(new ObjectId(recentGroupVo.getUserId())))
        );
        List<RecentGroupQueryVo> groupusers = mongoTemplate.aggregate(aggregation, "group_users", RecentGroupQueryVo.class).getMappedResults();
        List<MyMyGroupResultVo> res = new ArrayList<>();
        MyMyGroupResultVo item;
        for (RecentGroupQueryVo son : groupusers) {
            item = new MyMyGroupResultVo();
            BeanUtils.copyProperties(son, item);
            item.setGroupInfo(son.getGroupList().get(0));
            res.add(item);
        }
        return res;
    }

    @Override
    public List<MyGroupResultVo> getGroupUsersByGroupId(String groupId) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("groupId").is(new ObjectId(groupId))),
                Aggregation.lookup(
                        "users",
                        "userId",
                        "_id",
                        "userList"
                )
        );
        //根据群id去找所有的群成员信息
        List<MyGroupInfoQueryVo> queryVoList = mongoTemplate.aggregate(aggregation, "group_users", MyGroupInfoQueryVo.class).getMappedResults();
        List<MyGroupResultVo> res = new ArrayList<>();
        MyGroupResultVo item;
        for (MyGroupInfoQueryVo son : queryVoList) {
            item = new MyGroupResultVo();
            BeanUtils.copyProperties(son, item);
            item.setUserInfo(new SimpleUser());
            BeanUtils.copyProperties(son.getUserList().get(0), item.getUserInfo());
            res.add(item);
        }
        return res;
    }

    @Override
    public void addNewGroupUser(ValidateMessageResponseVo validateMessage) {
        GroupUser groupUser = groupUserDao.findGroupUserByUserIdAndGroupId(new ObjectId(validateMessage.getSenderId()), new ObjectId(validateMessage.getGroupInfo().getGroupId()));
        if (groupUser == null) {
            groupUser = new GroupUser();
            groupUser.setGroupId(new ObjectId(validateMessage.getGroupInfo().getGroupId()));
            groupUser.setUserId(new ObjectId(validateMessage.getSenderId()));
            groupUser.setUsername(validateMessage.getSenderName());
            groupUserDao.save(groupUser);
            Update update = new Update();
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(groupUser.getGroupId()));
            //群人数加一
            mongoTemplate.upsert(query, update.inc("userNum", 1), Group.class);
            //添加一条群消息
            GroupMessage groupMessage = new GroupMessage();
            groupMessage.setRoomId(groupUser.getGroupId().toString());
            //设置发送者id，为了退出群聊时比较方便删除!!!
            groupMessage.setSenderId(new ObjectId(validateMessage.getSenderId()));
            groupMessage.setMessageType("sys");
            groupMessage.setMessage(groupUser.getUsername() + "加入群聊");
            groupMessageDao.save(groupMessage);
        }
    }
}
