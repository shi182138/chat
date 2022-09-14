package com.example.chatt.controller;

import com.example.chatt.annotation.common.R;
import com.example.chatt.pojo.vo.HistoryMsgRequestVo;
import com.example.chatt.pojo.vo.IsReadMessageRequestVo;
import com.example.chatt.pojo.vo.SingleHistoryResultVo;
import com.example.chatt.pojo.vo.SingleMessageResultVo;
import com.example.chatt.service.SingleMessageService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SingleMessageController {
    @Autowired
    private SingleMessageService singleMessageService;

    @ApiOperation(value = "获取好友之间最后一条聊天记录")
    @GetMapping("/getLastMessage")
    public R getLastMessage(String roomId) {
        SingleMessageResultVo lastMessage = singleMessageService.getLastMessage(roomId);
        return R.ok().data("singleLastMessage",lastMessage);
    }

    @ApiOperation(value = "当用户在切换会话阅读消息后，标记该消息已读")
    @PostMapping("/isRead")
    public R userIsReadingMessage(@RequestBody IsReadMessageRequestVo isReadMessageRequestVo) {
        singleMessageService.userIsReadMessage(isReadMessageRequestVo);
        return R.ok();
    }
    @ApiOperation(value = "获取最近单聊消息")
    @GetMapping("/getRecentSingleMessages")
    public R getRecentSingleMessages(String roomId, Integer pageIndex, Integer pageSize) {
        List<SingleMessageResultVo> recentMessage = singleMessageService.getRecentMessage(roomId,pageIndex,pageSize);
        return R.ok().data("recentMessage", recentMessage);
    }
    @ApiOperation(value = "获取单聊历史记录")
    @PostMapping("/historyMessage")
    public R getSingleHistoryMessages(@RequestBody HistoryMsgRequestVo historyMsgRequestVo) {
        SingleHistoryResultVo singleHistoryResultVo = singleMessageService.getSingleHistoryMsg(historyMsgRequestVo);
        return R.ok().data("msgList", singleHistoryResultVo);
    }
    @ApiOperation(value = "获取真聊天记录")
    @GetMapping("/getHistoryMsg")
    public R getHistoryMsg(String roomId) {
        List<SingleMessageResultVo> historyMessage = singleMessageService.getRealHistory(roomId);
        return R.ok().data("msgList", historyMessage);
    }
}