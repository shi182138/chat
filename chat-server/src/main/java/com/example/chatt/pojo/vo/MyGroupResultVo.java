package com.example.chatt.pojo.vo;



import com.example.chatt.pojo.Group;
import com.example.chatt.pojo.SimpleUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyGroupResultVo {
    private String id;
    private String userId;
    private String username;
    private Integer manager;
    private Integer holder;
    private String card;
    private Date time;
//    private Group groupInfo;
    private List<Group> groupInfo;
    private String groupId;
    private SimpleUser userInfo;
}
