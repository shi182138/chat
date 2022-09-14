package com.example.chatt.service.impl;

import com.example.chatt.dao.UserDao;
import com.example.chatt.pojo.JwtUser;
import com.example.chatt.pojo.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Resource
    private UserDao userDao;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //查询用户信息
        User user = userDao.findUserByUsernameOrCode(username, username);
        if (user == null) throw new UsernameNotFoundException("用户名或密码错误");
        JwtUser jwtUser = new JwtUser(user);
        //查询权限信息
        return jwtUser;
    }
}
