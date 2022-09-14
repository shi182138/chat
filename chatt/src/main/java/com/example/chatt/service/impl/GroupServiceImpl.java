package com.example.chatt.service.impl;

import com.example.chatt.dao.AccountPoolDao;
import com.example.chatt.dao.GroupDao;
import com.example.chatt.dao.GroupUserDao;
import com.example.chatt.pojo.AccountPool;
import com.example.chatt.pojo.Group;
import com.example.chatt.pojo.GroupUser;
import com.example.chatt.pojo.vo.*;
import com.example.chatt.service.GroupService;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class GroupServiceImpl implements GroupService {
    @Autowired
    private GroupDao groupDao;
    @Autowired
    private GroupUserDao groupUserDao;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private AccountPoolDao accountPoolDao;
    @Override
    public Group getGroupInfo(String groupId) {
        Optional<Group> res = groupDao.findById(new ObjectId(groupId));
        return res.orElse(null);
    }

    @Override
    public List<SearchGroupResponseVo> searchGroup(SearchRequestVo requestVo, String userId) {
        Criteria criteria1 = Criteria.where("code").regex(Pattern.compile("^.*" + requestVo.getSearchContent() + ".*$", Pattern.CASE_INSENSITIVE));
        Criteria criteria2 = Criteria.where("title").regex(Pattern.compile("^.*" + requestVo.getSearchContent() + ".*$", Pattern.CASE_INSENSITIVE));
        Criteria criteria = new Criteria();
        criteria.orOperator(criteria2,criteria1);
        //-----------------
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.lookup(
                        "users",
                        "holderUserId",
                        "_id",
                        "holderUsers"
                ), Aggregation.match(
                        criteria
                ));
        List<SearchGroupResultVo> results = mongoTemplate.aggregate(aggregation, "groups", SearchGroupResultVo.class).getMappedResults();
        List<SearchGroupResponseVo> groups = new ArrayList<>();
        SearchGroupResponseVo item;
        for (SearchGroupResultVo son : results) {
            //群主账号不为当前登录的账号
            if (!son.getHolderUsers().get(0).getUserId().equals(userId)) {
                item = new SearchGroupResponseVo();
                BeanUtils.copyProperties(son, item);
                item.setGid(son.getId());
                BeanUtils.copyProperties(son.getHolderUsers().get(0), item.getHolderUserInfo());
                groups.add(item);
            }
        }
        return groups;
    }

    @Override
    public MyMyGroupResultVo createGroup(CreateGroupRequestVo requestVo) {
        AccountPool accountPool = new AccountPool();
        accountPool.setType(2);//群聊账号
        accountPool.setStatus(1);
        accountPoolDao.save(accountPool);
        //================ 插入到群集合中
        Group group = new Group();
        if (requestVo.getTitle() != null) group.setTitle(requestVo.getTitle());
        if (requestVo.getDesc() != null) group.setDesc(requestVo.getDesc());
        if (requestVo.getImg() != null) group.setImg(requestVo.getImg());
        group.setHolderName(requestVo.getHolderName());
        group.setHolderUserId(new ObjectId(requestVo.getHolderUserId()));
        //设置生成的code+偏移量
        group.setCode(String.valueOf(accountPool.getCode() + 10000000L));
        groupDao.save(group);
        //============== 建立群主消息
        GroupUser groupUser = new GroupUser();
        groupUser.setGroupId(group.getGroupId());
        groupUser.setUserId(group.getHolderUserId());
        groupUser.setUsername(group.getHolderName());
        if (requestVo.getCreateType() == 0) {
            groupUser.setManager(8);
        }
        groupUser.setHolder(1);
        groupUserDao.save(groupUser);
        //=====更新groups中gid的字段
        Update update = new Update();
        update.set("gid", group.getGroupId().toString());
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(group.getGroupId()));
        mongoTemplate.upsert(query, update, Group.class);


        MyMyGroupResultVo groupVo = new MyMyGroupResultVo();
        BeanUtils.copyProperties(groupUser,groupVo);
        groupVo.setGroupId(group.getGroupId().toString());
        groupVo.setUserId(group.getHolderUserId().toString());
        groupVo.setGroupInfo(group);
        groupVo.setId(String.valueOf(groupUser.getGuid()));
        return groupVo;
    }

    @Override
    public List<SearchGroupResultVo> getAllGroup() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.lookup(
                        "users",
                        "holderUserId",
                        "_id",
                        "holderUsers"
                )
        );
        AggregationResults<SearchGroupResultVo> groups = mongoTemplate.aggregate(aggregation, "groups", SearchGroupResultVo.class);
        return groups.getMappedResults();
    }

    @Override
    public void quitGroup(QuitGroupRequestVo requestVo) {
        //根据是否为群主进行分类
        if (requestVo.getHolder() == 1) { // 是群主
            //1、先删除该群中所有信息：(groupmessages)
            delGroupAllMessagesByGroupId(requestVo.getGroupId());
            //2、再删除该群的所有成员：（groupusers）
            delGroupAllUsersByGroupId(requestVo.getGroupId());
            //3、最后删除群账号：（groups）
            groupDao.deleteById(new ObjectId(requestVo.getGroupId()));
        } else { // 不是群主
            //1、先删除与当前用户发送的所有群信息
            delGroupMessagesByGroupIdAndSenderId(requestVo.getGroupId(), requestVo.getUserId());
            //2、再删除在该群的当前用户
            delGroupUserByGroupIdAndUserId(requestVo.getGroupId(), requestVo.getUserId());
            //3、群成员个数减1
            decrGroupUserNum(requestVo.getGroupId());
        }
    }

    @Override
    public void addGroupMember(AddGroupMemberVo addGroupMemberVo) {
        System.out.println("addGroupMember---------------------->");
        System.out.println(addGroupMemberVo.getMemberId());
        System.out.println(addGroupMemberVo.getMemberName());
//        Map<String ,String> user = addGroupMemberVo.getMembers();
        List<String> userIds = addGroupMemberVo.getMemberId();
        List<String> usernames = addGroupMemberVo.getMemberName();
//        List<String> userNames = addGroupMemberVo.getMemberName();
        ObjectId groupId = new ObjectId(addGroupMemberVo.getGroupId());
        GroupUser groupUser;
//        for (String key : user.keySet()) {
//            String name = user.get(key);
//            ObjectId userId = new ObjectId(key);
//            groupUser.setGroupId(groupId);
//            groupUser.setUserId(userId);
//            groupUser.setUsername(name);
//            System.out.println("addGroupMember---------------------->");
//            System.out.println(groupUser);
//            mongoTemplate.save(groupUser);
//        }
        for (int i=0; i<userIds.size(); i++) {
            groupUser =  new GroupUser();
            ObjectId id = new ObjectId(userIds.get(i));
//            String name = userNames.get(i);
            groupUser.setGroupId(groupId);
            groupUser.setUserId(id);
            groupUser.setUsername(usernames.get(i));
            System.out.println(groupUser);
            mongoTemplate.save(groupUser);
        }
        Update update = new Update();
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(groupId));
        //群人数加1
        mongoTemplate.upsert(query, update.inc("userNum", userIds.size()), Group.class);
    }

    private void delGroupAllMessagesByGroupId(String groupId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("roomId").is(groupId));
        DeleteResult groupmessages = mongoTemplate.remove(query, "groupMessage");
        // System.out.println("删除该群所有消息是否成功？" + groupMessage.getDeletedCount());
    }
    private void delGroupAllUsersByGroupId(String groupId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("groupId").is(new ObjectId(groupId)));
        DeleteResult groupusers = mongoTemplate.remove(query, "group_users");
        // System.out.println("删除该群所有成员是否成功？" + group_users.getDeletedCount());
    }
    private void delGroupMessagesByGroupIdAndSenderId(String groupId, String senderId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("roomId").is(groupId).and("senderId").is(new ObjectId(senderId)));
        DeleteResult groupmessages = mongoTemplate.remove(query, "groupMessage");
        // System.out.println("删除该用户所发的群消息是否成功？" + groupMessage.getDeletedCount());
    }

    private void delGroupUserByGroupIdAndUserId(String groupId, String userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("groupId").is(new ObjectId(groupId)).and("userId").is(new ObjectId(userId)));
        DeleteResult groupusers = mongoTemplate.remove(query, "group_users");
    }

    private void decrGroupUserNum(String gid) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(gid)));
        Update update = new Update();
        update.inc("userNum", -1); //该群人数减去1
        UpdateResult groups = mongoTemplate.upsert(query, update, "groups");
        // System.out.println("该群人数递减1是否成功？" + groups.getModifiedCount());
    }
}
