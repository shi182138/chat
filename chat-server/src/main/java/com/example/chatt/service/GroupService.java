package com.example.chatt.service;


import com.example.chatt.pojo.Group;
import com.example.chatt.pojo.vo.*;

import java.util.List;

public interface GroupService {
    Group getGroupInfo(String groupId);

    List<SearchGroupResponseVo> searchGroup(SearchRequestVo requestVo, String userId);

    MyMyGroupResultVo createGroup(CreateGroupRequestVo requestVo);

    List<SearchGroupResultVo> getAllGroup();

    void quitGroup(QuitGroupRequestVo requestVo);

    void addGroupMember(AddGroupMemberVo addGroupMemberVo);
}
