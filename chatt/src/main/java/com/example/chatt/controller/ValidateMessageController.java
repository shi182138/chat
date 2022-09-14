package com.example.chatt.controller;

import com.example.chatt.annotation.common.R;
import com.example.chatt.pojo.ValidateMessage;

import com.example.chatt.pojo.vo.ValidateMessageResponseVo;
import com.example.chatt.service.ValidateMessageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class ValidateMessageController {
    @Resource
    private ValidateMessageService validateMessageService;
    @GetMapping("/getValidateMessage")
    public R getValidateMessage(String roomId, Integer status, Integer validateType) {
        ValidateMessage validateMessage = validateMessageService.findValidateMessage(roomId, status, validateType);
        return R.ok().data("validateMessage",validateMessage);
    }

    //获取我的验证消息
    @GetMapping("/getMyValidateMessageList")
    public R getMyValidateMessageList(String userId) {
        List<ValidateMessageResponseVo> validateMessageResponseVoList = validateMessageService.getMyValidateMessageList(userId);
        return R.ok().data("myValidateMessageList",validateMessageResponseVoList);
    }
}
