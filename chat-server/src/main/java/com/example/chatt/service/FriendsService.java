package com.example.chatt.service;



import com.example.chatt.pojo.Friend;
import com.example.chatt.pojo.User_Friend;
import com.example.chatt.pojo.vo.DelGoodFriendRequestVo;
import com.example.chatt.pojo.vo.RecentConversationVo;
import com.example.chatt.pojo.vo.SingleRecentConversationVo;

import java.util.List;

public interface FriendsService {
     List<Friend> getMyFriendsList(String userId);

    List<SingleRecentConversationVo> getRecentConversation(RecentConversationVo recentConversationVo);

    void addFriend(User_Friend user_friend);

    void deleteFriend(DelGoodFriendRequestVo requestVo);
}
