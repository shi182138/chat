package com.example.chatt.service;

import com.example.chatt.annotation.common.R;
import com.example.chatt.pojo.vo.UserLoginParam;

import javax.servlet.http.HttpServletRequest;

public interface LoginService {
    R login(UserLoginParam userLoginParam, HttpServletRequest request);
}
