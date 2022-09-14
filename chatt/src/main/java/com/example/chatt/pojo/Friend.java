package com.example.chatt.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Friend {
    private String id;
    private String username;
    private String nickname;
    private String avatar;
    private Date createDate;
    private String roomId;
}
