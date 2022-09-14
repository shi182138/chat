package com.example.chatt.service;

import com.example.chatt.pojo.ValidateMessage;
import com.example.chatt.pojo.vo.ValidateMessageResponseVo;


import java.util.List;

public interface ValidateMessageService {
    ValidateMessage findValidateMessage(String roomId, Integer status, Integer validateType);

    List<ValidateMessageResponseVo> getMyValidateMessageList(String userId);

    void changeFriendValidateNewsStatus(String validateMessageId, Integer status);

    ValidateMessage addValidateMessage(ValidateMessage validateMessage);
}
