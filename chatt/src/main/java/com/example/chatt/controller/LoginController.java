package com.example.chatt.controller;
import com.example.chatt.annotation.common.R;
import com.example.chatt.pojo.vo.UserLoginParam;
import com.example.chatt.service.LoginService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Api(tags = "LoginController")
@RestController
public class LoginController {
    @Resource
    private LoginService loginService;

    @PostMapping(value = "user/login")
    public R login(@RequestBody UserLoginParam userLoginParam, HttpServletRequest request) {
        return loginService.login(userLoginParam,request);
    }
}
