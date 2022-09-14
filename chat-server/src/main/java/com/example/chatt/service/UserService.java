package com.example.chatt.service;

import com.example.chatt.annotation.common.R;
import com.example.chatt.pojo.User;
import com.example.chatt.pojo.vo.*;

import java.util.List;
import java.util.Map;

public interface UserService {
    Map<String, Object> register(RegisterRequestVo rVo);

    R logout();

    User getUserInfo(String username);

    R updateUserFace(String url, String id);

    List<User> getUserList();

    List<User> searchUser(SearchRequestVo requestVo);

    void changeUserStatus(String uid, Integer status);

    List<User> getUsersBySignUpTime(String lt, String rt);

    User getUserInfoById(String uid);

    void addNewFenZu(NewFenZuRequestVo requestVo);

    void modifyFriendFenZu(ModifyFriendFenZuRequestVo requestVo);

    void deleteFenZu(DelFenZuRequestVo requestVo);

    void editFenZu(EditFenZuRequestVo requestVo);

    void modifyBeiZhu(ModifyFriendBeiZhuRequestVo requestVo);

    void addFenZuMembers(AddFenZuMemberRequestVo requestVo);
}
