package com.example.chatt.pojo.vo;

import com.example.chatt.pojo.Group;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecentGroupQueryVo {
    private String id;
    private String groupId;
    private String userId;
    private String username;
    private Integer manager;
    private Integer holder;
    private String card;
    private Date time;
    private List<Group> groupList;
}
