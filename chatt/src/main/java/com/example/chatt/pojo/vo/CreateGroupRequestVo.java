package com.example.chatt.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateGroupRequestVo {
    private String title;
    private String desc;
    private String img;
    private String holderName;
    private String holderUserId;
    private Integer createType; //0创建空群/1好友创建
}
