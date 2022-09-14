package com.example.chatt.service;

import com.example.chatt.pojo.SystemUser;
import com.example.chatt.pojo.vo.SystemUserResponseVo;

import java.util.List;

public interface SysService {
    List<SystemUserResponseVo> getSysUsers();
    void notExistThenAddSystemUser(SystemUser user);
}
