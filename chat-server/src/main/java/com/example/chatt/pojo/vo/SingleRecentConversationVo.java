package com.example.chatt.pojo.vo;

import com.example.chatt.pojo.SimpleUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SingleRecentConversationVo {
    private String id;
    private String createDate;//用字符串显示
    private SimpleUser userM;
    private SimpleUser userY;
}
