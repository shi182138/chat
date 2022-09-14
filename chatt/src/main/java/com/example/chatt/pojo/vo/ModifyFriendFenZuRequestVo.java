package com.example.chatt.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModifyFriendFenZuRequestVo {
    private String userId;
    private String friendId;
    private String newFenZuName;
}
