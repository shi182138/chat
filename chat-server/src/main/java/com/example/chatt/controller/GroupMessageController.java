package com.example.chatt.controller;

import com.example.chatt.annotation.common.R;
import com.example.chatt.pojo.vo.GroupHistoryResultVo;
import com.example.chatt.pojo.vo.GroupMessageResultVo;
import com.example.chatt.pojo.vo.HistoryMsgRequestVo;
import com.example.chatt.service.GroupMessageService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class GroupMessageController {
    @Resource
    private GroupMessageService groupMessageService;

    /**
     * 获取最近的群消息
     */
    @ApiOperation(value = "获取最近的群消息")
    @GetMapping("/getRecentGroupMessages")
    public R getRecentGroupMessages(String roomId, Integer pageIndex, Integer pageSize) {
        List<GroupMessageResultVo> recentGroupMessages = groupMessageService.getRecentGroupMessages(roomId, pageIndex, pageSize);
        return R.ok().data("recentGroupMessages", recentGroupMessages);
    }

    /**
     * 获取群历史消息
     */
    @ApiOperation(value = "获取群历史消息")
    @PostMapping("/historyMessages")
    public R getGroupHistoryMessages(@RequestBody HistoryMsgRequestVo historyMsgRequestVo) {
        GroupHistoryResultVo historyMessages = groupMessageService.getGroupHistoryMessages(historyMsgRequestVo);
        return R.ok().data("total", historyMessages.getCount()).data("msgList", historyMessages.getGroupMessages());
    }
    @ApiOperation(value = "获取真群历史消息")
    @GetMapping("/groupHistoryMsg")
    public R getGroupHistoryMsg(String roomId) {
        List<GroupMessageResultVo> historyMessages = groupMessageService.getRealGroupHistoryMsg(roomId);
        return R.ok().data("msgList", historyMessages);
    }

    /**
     * 获取群最后一条消息
     */
    @ApiOperation(value = "获取群最后一条消息")
    @GetMapping("/lastMessage")
    public R getGroupLastMessage(String roomId) {
        GroupMessageResultVo groupLastMessage = groupMessageService.getGroupLastMessage(roomId);
        return R.ok().data("groupLastMessage", groupLastMessage);
    }
}
