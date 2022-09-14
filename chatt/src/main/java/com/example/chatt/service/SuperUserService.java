package com.example.chatt.service;

import com.example.chatt.annotation.common.ResultEnum;
import com.example.chatt.dao.SuperUserDao;
import com.example.chatt.pojo.SuperUser;
import com.example.chatt.utils.JwtTokenUtil;
import com.example.chatt.utils.RedisCache;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class SuperUserService {
    @Resource
    private SuperUserDao superUserDao;
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Resource
    private RedisCache redisCache;

    public Map<String, Object> superUserLogin(SuperUser superUser) {
        Map<String, Object> map = new HashMap<>();
        Integer code = null;
        String msg = null;
        SuperUser user = existSuperUser(superUser.getAccount());
        if (user == null) {
            msg = ResultEnum.ACCOUNT_NOT_FOUND.getMessage();
            code = ResultEnum.ACCOUNT_NOT_FOUND.getCode();
        } else {
            if (!bCryptPasswordEncoder.matches(superUser.getPassword(), user.getPassword())) {//使用matches方法（参数1：不经过加密的密码，参数2：已加密密码）
                code = ResultEnum.OLD_PASSWORD_ERROR.getCode();
                msg = ResultEnum.OLD_PASSWORD_ERROR.getMessage();
            } else {
                // 登录成功，不仅要创建token，而且要将其和用户信息一起返回
                code = ResultEnum.LOGIN_SUCCESS.getCode();
                msg = ResultEnum.LOGIN_SUCCESS.getMessage();
                JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();
                String jwt = jwtTokenUtil.createToken(user.getAccount());
                map.put("userInfo", user);
                map.put("token", jwt);
                //存入redis
                redisCache.setCacheObject("login"+user.getAccount(), user);
            }
        }
        map.put("code", code);
        map.put("msg", msg);
        return map;
    }

    public SuperUser existSuperUser(String account) {
        Query query = new Query();
        query.addCriteria(Criteria.where("account").is(account));
        return mongoTemplate.findOne(query, SuperUser.class);
    }

    public void notExistThenAddSuperUser(SuperUser superUser) {
        SuperUser existSuperUser = existSuperUser(superUser.getAccount());
        if (existSuperUser == null) {
            addSuperUser(superUser);
        }
    }
    public void addSuperUser(SuperUser superUser) {
        //对密码进行加密
        superUser.setPassword(bCryptPasswordEncoder.encode(superUser.getPassword()));
        superUserDao.save(superUser);
    }
}
