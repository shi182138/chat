package com.example.chatt.service;


import com.example.chatt.pojo.GroupMessage;
import com.example.chatt.pojo.vo.GroupHistoryResultVo;
import com.example.chatt.pojo.vo.GroupMessageResultVo;
import com.example.chatt.pojo.vo.HistoryMsgRequestVo;

import java.util.List;

public interface GroupMessageService {
    void addNewGroupMessage(GroupMessage groupMessage);

    List<GroupMessageResultVo> getRecentGroupMessages(String roomId, Integer pageIndex, Integer pageSize);

    GroupHistoryResultVo getGroupHistoryMessages(HistoryMsgRequestVo historyMsgRequestVo);

    GroupMessageResultVo getGroupLastMessage(String roomId);

    List<GroupMessageResultVo> getRealGroupHistoryMsg(String roomId);
}
