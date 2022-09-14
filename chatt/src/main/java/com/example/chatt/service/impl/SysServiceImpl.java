package com.example.chatt.service.impl;

import com.example.chatt.dao.SysDao;
import com.example.chatt.pojo.SystemUser;
import com.example.chatt.pojo.vo.SystemUserResponseVo;
import com.example.chatt.service.SysService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SysServiceImpl implements SysService {
    @Autowired
    private SysDao sysDao;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<SystemUserResponseVo> getSysUsers() {
        List<SystemUser> systemUsers = sysDao.findAll();
        List<SystemUserResponseVo> responseVoList = new ArrayList<>();
        SystemUserResponseVo item;
        for (SystemUser systemUser : systemUsers) {
            item = new SystemUserResponseVo();
            BeanUtils.copyProperties(systemUser,item);
            item.setSid(systemUser.getId());
            responseVoList.add(item);
        }
        return responseVoList;
    }

    //检查
    @Override
    public void notExistThenAddSystemUser(SystemUser user) {
        Query query = new Query();
        query.addCriteria(Criteria.where("code").is(user.getCode()));
        SystemUser one = mongoTemplate.findOne(query, SystemUser.class);
        if (one == null) {
            sysDao.save(user);
        }
    }
}
