package com.example.chatt.controller;

import com.example.chatt.annotation.common.R;

import com.example.chatt.annotation.common.ResultEnum;
import com.example.chatt.pojo.Friend;
import com.example.chatt.pojo.JwtUser;
import com.example.chatt.pojo.vo.DelGoodFriendRequestVo;
import com.example.chatt.pojo.vo.RecentConversationVo;
import com.example.chatt.pojo.vo.SingleRecentConversationVo;
import com.example.chatt.service.FriendsService;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class FriendController {
    @Resource
    private FriendsService friendsService;
    //查询好友列表
    @ApiOperation(value = "获取好友列表")
    @GetMapping("/getMyFriendsList")
    public R getMyFriendsList(String userId){
        List<Friend> myFriendsList = friendsService.getMyFriendsList(userId);
        return R.ok().data("myFriendsList",myFriendsList);
    }
    //查询最近好友列表
    @ApiOperation(value = "查询最近好友列表")
    @PostMapping("/recentConversationList")
    public R getRecentConversationList(@RequestBody RecentConversationVo recentConversationVo) {
        List<SingleRecentConversationVo> resultList = friendsService.getRecentConversation(recentConversationVo);
        return R.ok().data("singleRecentConversationList",resultList);
    }

    @ApiOperation(value = "删除好友")
    @DeleteMapping("/deleteGoodFriend")
    public R deleteGoodFriend(@RequestBody DelGoodFriendRequestVo requestVo) {
    String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // 这个 principal 跟校验token时保存认证信息有关
    if (!userId.equals(requestVo.getUserM())) return R.error().resultEnum(ResultEnum.ILLEGAL_OPERATION); //不是本人，非法操作
    friendsService.deleteFriend(requestVo);
    return R.ok().message("删除好友成功");
    }

}
