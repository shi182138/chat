package com.example.chatt.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupHistoryResultVo {
    private List<GroupMessageResultVo> groupMessages;
    private Long count;
}
