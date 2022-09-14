package com.example.chatt.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleUser {
    private String userId;
    private String avatar;
    private String signature;
    private String nickname;
    private Long onlineTime;
    private Date lastLoginTime;
    private String username;
}
