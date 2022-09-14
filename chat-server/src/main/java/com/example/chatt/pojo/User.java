package com.example.chatt.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private String userId;
    @Indexed(unique = true)
    private String username;
    @Indexed(unique = true)
    private String password;
    @Indexed(unique = true)
    private String code;
    private String avatar = ""; //头像
    private String nickname;
    private String email;
    private Province province = new Province();
    private City city = new City();
    private Town town = new Town();
    private Integer sex = 3; // 0 男 1 女 3 保密（默认值）
    private Double opacity = 0.75D; //聊天框透明度
    private Integer blur = 10; //模糊度
    private String bgImg = "abstract"; //背景图种类名
    private String customBgImgUrl = ""; //自定义的背景图访问链接
    private String notifySound = "default"; //提示音
    private String color = "#000"; //字体颜色
    private String bgColor = "#fff"; //背景颜色
    private Date signUpTime = new Date();  // 注册时间
    private Date lastLoginTime = new Date(); // 最后一次登录
    private Integer status = 0; // 0：正常可用，1：冻结不可用，2：注销不可用
    private Integer age = 18;
    private Long onlineTime = 0L; //在线时长
    private BrowserSetting loginSetting; //登录设备信息
    //分组信息，默认添加“我的好友” 这个分组
    private Map<String, ArrayList<String>> friendFenZu = new HashMap<String, ArrayList<String>>() {
        {
            put("我的好友", new ArrayList<>());
        }
    };
    //好友备注信息
    private Map<String, String> friendBeiZhu = new HashMap<>();
}

