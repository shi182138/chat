package com.example.chatt.service.impl;


import com.example.chatt.dao.FriendDao;

import com.example.chatt.dao.UserDao;
import com.example.chatt.pojo.*;
import com.example.chatt.pojo.vo.DelGoodFriendRequestVo;
import com.example.chatt.pojo.vo.RecentConversationVo;
import com.example.chatt.pojo.vo.SingleRecentConversationVo;
import com.example.chatt.service.FriendsService;
import com.example.chatt.utils.DateUtil;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class FriendsServiceImpl implements FriendsService {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private FriendDao friendDao;
    @Resource
    private UserDao userDao;
    @Override
    public List<Friend> getMyFriendsList(String userId) {
        Aggregation aggregation1 = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("userM").is(new ObjectId(userId))),
                Aggregation.lookup(
                        "users",
                        "userY",
                        "_id",
                        "uList"
                )
        );
        Aggregation aggregation2 = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("userY").is(new ObjectId(userId))),
                Aggregation.lookup(
                        "users",
                        "userM",
                        "_id",
                        "uList"
                )
        );
        List<Friend> resList = new ArrayList<>();
        List<QueryFriendResult> friends1 = mongoTemplate.aggregate(aggregation1,"user_friend", QueryFriendResult.class).getMappedResults();
        List<QueryFriendResult> friends2 = mongoTemplate.aggregate(aggregation2,"user_friend",QueryFriendResult.class).getMappedResults();
        Friend item;
        for (QueryFriendResult friend : friends1) {
            System.out.println(friend);
            item = new Friend();
            item.setUsername(friend.getUList().get(0).getUsername());
            item.setNickname(friend.getUList().get(0).getNickname());
            item.setAvatar(friend.getUList().get(0).getAvatar());
            item.setId(friend.getUList().get(0).getUserId());
            item.setRoomId(userId + "-" + friend.getUList().get(0).getUserId());
            item.setCreateDate(friend.getCreateDate());
            resList.add(item);
        }
        for (QueryFriendResult friend : friends2) {
            item = new Friend();
            item.setUsername(friend.getUList().get(0).getUsername());
            item.setNickname(friend.getUList().get(0).getNickname());
            System.out.println("-----------------" );
            item.setAvatar(friend.getUList().get(0).getAvatar());
            item.setId(friend.getUList().get(0).getUserId());
            item.setRoomId(friend.getUList().get(0).getUserId() + "-" + userId);
            item.setCreateDate(friend.getCreateDate());
            resList.add(item);
        }
        return resList;
    }

    @Override
    public List<SingleRecentConversationVo> getRecentConversation(RecentConversationVo recentConversationVo) {
        List<ObjectId> friendsId = new ArrayList<>();
        for (String friendId : recentConversationVo.getRecentFriendIds()) {
            friendsId.add(new ObjectId(friendId));
        }
        System.out.println("friendsId------->" + friendsId);
        Criteria criteria1 = Criteria.where("userM").in(friendsId).and("userY").is(new ObjectId(recentConversationVo.getUserId()));
        Criteria criteria2 = Criteria.where("userY").in(friendsId).and("userM").is(new ObjectId(recentConversationVo.getUserId()));
        Criteria criteria = new Criteria();
        criteria.orOperator(criteria1,criteria2);
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(
                        criteria
                ),
                Aggregation.lookup(
                        "users",
                        "userY",
                        "_id",
                        "uList1"
                ),
                Aggregation.lookup(
                        "users",
                        "userM",
                        "_id",
                        "uList2"
                )
        );
        List<QueryFriendResult> recentFriends = mongoTemplate.aggregate(aggregation,"user_friend",QueryFriendResult.class).getMappedResults();
//        System.out.println("recentFriends----------------->" + recentFriends);
        List<SingleRecentConversationVo> resultVoList = new ArrayList<>();
        SingleRecentConversationVo item;
        SimpleUser userM,userY;
        for (QueryFriendResult friend : recentFriends) {
            item = new SingleRecentConversationVo();
            System.out.println(friend.getCreateDate());
            item.setCreateDate(DateUtil.format(friend.getCreateDate(),DateUtil.yyyy_MM_dd_HH_mm_ss));
            item.setId(friend.getId());
            userM = new SimpleUser();
            userY = new SimpleUser();
            if (friend.getUList1().get(0).getUserId().equals(friend.getUserM())) {
                BeanUtils.copyProperties(friend.getUList1().get(0),userM);
                BeanUtils.copyProperties(friend.getUList2().get(0),userY);
            }else {
                BeanUtils.copyProperties(friend.getUList1().get(0),userY);
                BeanUtils.copyProperties(friend.getUList2().get(0),userM);
            }
            item.setUserM(userM);
            item.setUserY(userY);
            resultVoList.add(item);
        }
        return resultVoList;

    }

    @Override
    public void addFriend(User_Friend user_friend) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userM").is(user_friend.getUserM())
                .and("userY").is(user_friend.getUserY())
        );
        User_Friend one = mongoTemplate.findOne(query,User_Friend.class);
        if (one == null) {
            friendDao.save(user_friend);
        }
    }

    @Override
    public void deleteFriend(DelGoodFriendRequestVo requestVo) {
        //默认userM是主动删除者的ID，userY是被动删除者的ID
        Criteria criteriaA = Criteria.where("userY").is(new ObjectId(requestVo.getUserM())).and("userM").is(new ObjectId(requestVo.getUserY()));
        Criteria criteriaB = Criteria.where("userM").is(new ObjectId(requestVo.getUserM())).and("userY").is(new ObjectId(requestVo.getUserY()));
        Criteria criteria = new Criteria();
        criteria.orOperator(criteriaA, criteriaB);
        Query query = new Query();
        query.addCriteria(criteria);
        mongoTemplate.findAndRemove(query, User_Friend.class);
        //根据 roomId 删除两者的聊天记录
        delSingleHistoryMessage(requestVo.getRoomId());
        //删除该好友表中对应的分组信息和备注信息，都要互相更改双方的分组信息
        delFriendFenZuAndBeiZhu(requestVo.getUserM(), requestVo.getUserY());
        delFriendFenZuAndBeiZhu(requestVo.getUserY(), requestVo.getUserM());
    }
    private void delSingleHistoryMessage(String roomId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("roomId").is(roomId));
        mongoTemplate.remove(query, "single_messages");
    }

    private User getUser(String uid) {
        return userDao.findById(new ObjectId(uid)).orElse(null);
    }

    /**
     * 删除 users 表中好友分组信息和备注西悉尼
     *
     * @param myId     当前登录的用户id
     * @param friendId 被删除好友 id
     */
    private void delFriendFenZuAndBeiZhu(String myId, String friendId) {
        User userInfo = getUser(myId);
        boolean flag = false;
        Map<String, ArrayList<String>> friendFenZuMap = userInfo.getFriendFenZu();
        // System.out.println("分组map：" + friendFenZuMap);
        for (Map.Entry<String, ArrayList<String>> item : friendFenZuMap.entrySet()) {
            Iterator<String> iterator = item.getValue().iterator();
            while (iterator.hasNext()) {
                if (iterator.next().equals(friendId)) {
                    //原来已经在某个分组就从中去掉
                    iterator.remove();
                    flag = true;//没必要再循环了
                    break;
                }
            }
            if (flag) break;
        }
        Map<String, String> friendBeiZhuMap = userInfo.getFriendBeiZhu();
        // System.out.println("备注map：" + friendBeiZhuMap);
        friendBeiZhuMap.remove(friendId);

        //更新用户信息
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(myId)));
        Update update = new Update();
        update.set("friendFenZu", friendFenZuMap).set("friendBeiZhu", friendBeiZhuMap);
        mongoTemplate.findAndModify(query, update, User.class);
    }
}
