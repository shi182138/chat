package com.example.chatt.controller;

import com.example.chatt.annotation.common.R;
import com.example.chatt.annotation.common.ResultEnum;
import com.example.chatt.pojo.Group;
import com.example.chatt.pojo.vo.*;
import com.example.chatt.service.GroupService;
import com.example.chatt.service.GroupUserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class GroupController {
    @Autowired
    private GroupUserService groupUserService;

    @Autowired
    private GroupService groupService;
    /**
     * 根据用户名获取我的群聊列表
     */
    @ApiOperation(value = "根据用户id获取我的群聊列表")
    @GetMapping("/getMyGroupList")
    public R getMyGroupList(String userId) {
        List<MyGroupResultVo> myGroupList = groupUserService.getGroupUsersByUserId(userId);
        // System.out.println("我的群聊列表为：" + myGroupList);
        return R.ok().data("myGroupList", myGroupList);
    }


    /**
     * 获取最近的群聊
     */
    @ApiOperation(value = "获取最近的群聊")
    @PostMapping("/recentGroup")
    public R getRecentGroup(@RequestBody RecentGroupVo recentGroupVo) {
        // System.out.println("最近的群聊列表请求参数为：" + recentGroupVo);
        List<MyMyGroupResultVo> recentGroups = groupUserService.getRecentGroup(recentGroupVo);
        // System.out.println("最近的群聊列表为：" + recentGroups);
        return R.ok().data("recentGroups", recentGroups);
    }

    /**
     * 获取群聊详情
     */
    @ApiOperation(value = "获取群聊详情")
    @GetMapping("/getGroupInfo")
    public R getGroupInfo(String groupId) {
        Group groupInfo = groupService.getGroupInfo(groupId);
        // System.out.println("查询出的群消息为：" + groupInfo);
        List<MyGroupResultVo> groupUsers = groupUserService.getGroupUsersByGroupId(groupId);
        // System.out.println("群聊详情为：" + groupUsers);
        return R.ok().data("groupInfo", groupUsers);
    }

    /**
     * 在客户端搜索群聊
     */
    @ApiOperation(value = "在客户端搜索群聊")
    @PostMapping("/preFetchGroup")
    public R searchGroup(@RequestBody SearchRequestVo requestVo) {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // 这个 principal 跟校验token时保存认证信息有关
        List<SearchGroupResponseVo> groupResponseVos = groupService.searchGroup(requestVo, userId);
        return R.ok().data("groupList", groupResponseVos);
    }

    /**
     * 创建群聊
     */
    @ApiOperation(value = "创建群聊")
    @PostMapping("/createGroup")
    public R createGroup(@RequestBody CreateGroupRequestVo requestVo) {
        MyMyGroupResultVo groupCreator = groupService.createGroup(requestVo);
        return R.ok().data("groupCreator", groupCreator);
    }
    @ApiOperation(value = "添加群成员")
    @PostMapping("/addGroupMember")
    public R addGroupMember(@RequestBody AddGroupMemberVo addGroupMemberVo) {
        groupService.addGroupMember(addGroupMemberVo);
        return R.ok();
    }


    /**
     * 获取所有群聊
     */
    @ApiOperation(value = "获取所有群聊")
    @GetMapping("/all")
    public R getAllGroup() {
        List<SearchGroupResultVo> allGroup = groupService.getAllGroup();
        return R.ok().data("allGroup", allGroup);
    }

    /**
     * 退出群聊
     */
    @ApiOperation(value = "退出群聊")
    @PostMapping("/quitGroup")
    public R quitGroup(@RequestBody QuitGroupRequestVo requestVo) {
        // System.out.println("退出群聊的请求参数为：" + requestVo);

        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // 这个 principal 跟校验token时保存认证信息有关
        System.out.println("我是退出群聊");
        System.out.println(userId);
        if (!userId.equals(requestVo.getUserId()))
            //当前操作人不匹配，非法操作
            return R.error().resultEnum(ResultEnum.ILLEGAL_OPERATION);
        groupService.quitGroup(requestVo);
        return R.ok();
    }
}
