package com.example.chatt.service;

import com.example.chatt.pojo.vo.MyGroupResultVo;
import com.example.chatt.pojo.vo.MyMyGroupResultVo;
import com.example.chatt.pojo.vo.RecentGroupVo;
import com.example.chatt.pojo.vo.ValidateMessageResponseVo;


import java.util.List;

public interface GroupUserService {
    List<MyGroupResultVo> getGroupUsersByUserId(String username);

    List<MyMyGroupResultVo> getRecentGroup(RecentGroupVo recentGroupVo);

    List<MyGroupResultVo> getGroupUsersByGroupId(String groupId);

    void addNewGroupUser(ValidateMessageResponseVo validateMessage);
}
