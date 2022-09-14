package com.example.chatt.service.impl;

import com.example.chatt.dao.SingleMessageDao;
import com.example.chatt.pojo.SingleMessage;
import com.example.chatt.pojo.vo.HistoryMsgRequestVo;
import com.example.chatt.pojo.vo.IsReadMessageRequestVo;
import com.example.chatt.pojo.vo.SingleHistoryResultVo;
import com.example.chatt.pojo.vo.SingleMessageResultVo;
import com.example.chatt.service.SingleMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

@Service
public class SingleMessageServiceImpl implements SingleMessageService {
    @Autowired
    private SingleMessageDao singleMessageDao;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<SingleMessageResultVo> getRecentMessage(String roomId, Integer pageIndex, Integer pageSize) {
        Query query = new Query();
        query.addCriteria(Criteria.where("roomId").is(roomId))
                .with(Sort.by(Sort.Direction.DESC,"_id"))
                .skip(pageIndex * pageSize)
                .limit(pageSize);
        return mongoTemplate.find(query, SingleMessageResultVo.class, "single_messages");
    }

    @Override
    public void addNewSingleMessage(SingleMessage singleMessage) {
        singleMessageDao.save(singleMessage);
    }

    @Override
    public SingleMessageResultVo getLastMessage(String roomId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("roomId").is(roomId))
                .with(Sort.by(Sort.Direction.DESC,"_id"));
        SingleMessageResultVo message = mongoTemplate.findOne(query, SingleMessageResultVo.class,"single_messages");
        if (message == null) {
            message = new SingleMessageResultVo();
            message.setRoomId(roomId);
            message.setMessageType("text");
            message.setMessage("");
        }
        return message;
    }

    @Override
    public void userIsReadMessage(IsReadMessageRequestVo isReadMessageRequestVo) {
        Update update = new Update();
        update.addToSet("isReadUser",isReadMessageRequestVo.getUserId());
        Query query = new Query();
        query.addCriteria(Criteria.where("roomId").is(isReadMessageRequestVo.getRoomId()));
        mongoTemplate.updateMulti(query,update,"single_messages");
    }

    @Override
    public SingleHistoryResultVo getSingleHistoryMsg(HistoryMsgRequestVo historyMsgRequestVo) {
        Criteria criteria1 = new Criteria();
        criteria1.and("roomId").is(historyMsgRequestVo.getRoomId());
        Criteria criteria2 = null;
        if (!historyMsgRequestVo.getType().equals("all")) {
            criteria1.and("messageType").is(historyMsgRequestVo.getType())
                    .and("fileRawName").regex(Pattern.compile("^.*" + historyMsgRequestVo.getQuery() + ".*$", Pattern.CASE_INSENSITIVE));
        } else {
            criteria2 = new Criteria().orOperator(Criteria.where("message").regex(Pattern.compile("^.*" + historyMsgRequestVo.getQuery() + ".*$", Pattern.CASE_INSENSITIVE)),
                    Criteria.where("fileRawName").regex(Pattern.compile("^.*" + historyMsgRequestVo.getQuery() + ".*$", Pattern.CASE_INSENSITIVE)));
        }
        if (historyMsgRequestVo.getDate() != null) {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(historyMsgRequestVo.getDate());
            calendar.add(Calendar.DATE, 1);
            Date tomorrow = calendar.getTime();
            criteria1.and("time").gte(historyMsgRequestVo.getDate()).lt(tomorrow);
        }
        Query query = new Query();
        if (criteria2 != null) query.addCriteria(new Criteria().andOperator(criteria1, criteria2));
        else query.addCriteria(criteria1);
        Long total =  mongoTemplate.count(query, SingleHistoryResultVo.class, "single_messages");
        query.skip(historyMsgRequestVo.getPageIndex() * historyMsgRequestVo.getPageSize()); //页码
        query.limit(historyMsgRequestVo.getPageSize()); //每页显示数量
        List<SingleMessageResultVo> messageList = mongoTemplate.find(query, SingleMessageResultVo.class, "single_messages"); //必须带上集合名称
        return new SingleHistoryResultVo(messageList, total);
    }

    @Override
    public List<SingleMessageResultVo> getRealHistory(String roomId) {
        System.out.println(roomId);
        Query query = new Query();
        query.addCriteria(Criteria.where("roomId").is(roomId))
                .with(Sort.by(Sort.Direction.DESC,"_id"));
        return mongoTemplate.find(query, SingleMessageResultVo.class, "single_messages");
    }
}
