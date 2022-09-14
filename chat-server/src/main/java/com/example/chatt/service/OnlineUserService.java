package com.example.chatt.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Set;

@Service
public class OnlineUserService {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public int countOnlineUser() {
        String onlineUidSetKey = "online:uidSet";
        Set<Object> members = redisTemplate.opsForSet().members(onlineUidSetKey);
        return members == null ? 0 : members.size();
    }
}
