package com.example.chatt.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecentConversationVo {
    private String userId;
    private List<String> recentFriendIds;
}
