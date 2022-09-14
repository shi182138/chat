package com.example.chatt.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document("system_users")
public class SystemUser {
    @Id
    private String id;
    private String code;
    private String nickname;
    private Integer status;
}
