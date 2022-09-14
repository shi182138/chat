package com.example.chatt.service.impl;

import com.example.chatt.annotation.common.R;
import com.example.chatt.annotation.common.ResultEnum;
import com.example.chatt.pojo.JwtUser;
import com.example.chatt.pojo.vo.UserLoginParam;
import com.example.chatt.service.LoginService;
import com.example.chatt.utils.JwtTokenUtil;
import com.example.chatt.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Resource
    private RedisCache redisCache;
    @Resource
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public R login(UserLoginParam userLoginParam, HttpServletRequest request) {
        String vcCode = userLoginParam.getVcCode();
        String captcha = (String) request.getSession().getAttribute("captcha");
        if (StringUtils.isEmpty(vcCode) || !captcha.equalsIgnoreCase(vcCode)) {
            return R.error().resultEnum(ResultEnum.CAPTCHA_TIME_OUT_OR_ERROR);
//            return R.error();
        }
        //登录
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userLoginParam.getUsername(),userLoginParam.getPassword());
        Authentication authentication = null;
        try {
            authentication = authenticationManager.authenticate(authenticationToken);
        } catch (AuthenticationException e) {
            return R.error().resultEnum(ResultEnum.USER_LOGIN_FAILED);
        }
        JwtUser jwtUser = (JwtUser) authentication.getPrincipal();
        String username = jwtUser.getUser().getUsername();
        String jwt = jwtTokenUtil.generateToken(jwtUser);
        //把用户信息存入redis
        redisCache.setCacheObject("login"+username,jwtUser);
        return R.ok().resultEnum(ResultEnum.LOGIN_SUCCESS).data("token",jwt);
    }
}
