package com.example.chatt.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddGroupMemberVo {
    private Map<String ,String> members;
    private Map<String ,String> memberPlus = new HashMap<>();
    private List<String> memberId;
    private List<String> memberName;
    private String groupId;
}
