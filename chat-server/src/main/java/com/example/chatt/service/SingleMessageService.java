package com.example.chatt.service;

import com.example.chatt.pojo.SingleMessage;
import com.example.chatt.pojo.vo.HistoryMsgRequestVo;
import com.example.chatt.pojo.vo.IsReadMessageRequestVo;
import com.example.chatt.pojo.vo.SingleHistoryResultVo;
import com.example.chatt.pojo.vo.SingleMessageResultVo;

import java.util.List;

public interface SingleMessageService {
    public List<SingleMessageResultVo> getRecentMessage(String roomId, Integer pageIndex, Integer pageSize);

    void addNewSingleMessage(SingleMessage singleMessage);

    SingleMessageResultVo getLastMessage(String roomId);

    void userIsReadMessage(IsReadMessageRequestVo isReadMessageRequestVo);

    SingleHistoryResultVo getSingleHistoryMsg(HistoryMsgRequestVo historyMsgRequestVo);

    List<SingleMessageResultVo> getRealHistory(String roomId);
}
