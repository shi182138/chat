package com.example.chatt.service.impl;

import com.example.chatt.annotation.common.ConstValueEnum;
import com.example.chatt.annotation.common.R;
import com.example.chatt.annotation.common.ResultEnum;
import com.example.chatt.dao.AccountPoolDao;
import com.example.chatt.dao.UserDao;
import com.example.chatt.pojo.AccountPool;
import com.example.chatt.pojo.JwtUser;
import com.example.chatt.pojo.User;
import com.example.chatt.pojo.vo.*;
import com.example.chatt.service.UserService;
import com.example.chatt.utils.DateUtil;
import com.example.chatt.utils.RedisCache;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private AccountPoolDao accountPoolDao;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public Map<String, Object> register(RegisterRequestVo registerRequestVo) {

        System.out.println("-----------------------------------------------------------");
        Map<String, Object> map = new HashMap<>();
        Integer code = null;
        String msg = null;
        String userCode = null;
        User existUser = userDao.findUserByUsername(registerRequestVo.getUsername());
        if (existUser != null) {
            code = ResultEnum.USER_HAS_EXIST.getCode();
            msg = ResultEnum.USER_HAS_EXIST.getMessage();
        }else {
            //生成用户唯一标识账号（池code
            AccountPool accountPool = new AccountPool();
            accountPool.setType(ConstValueEnum.USERTYPE);
            accountPool.setStatus(ConstValueEnum.ACCOUNT_USED);
            accountPoolDao.save(accountPool);
            //将用户信息存入数据库
            String password = bCryptPasswordEncoder.encode(registerRequestVo.getPassword());
            User user = new User();
            user.setUsername(registerRequestVo.getUsername());
            user.setPassword(password);
            user.setCode(String.valueOf(accountPool.getCode() + ConstValueEnum.INITIAL_NUMBER));
            user.setAvatar(registerRequestVo.getAvatar());
//          user.setNickname(); //随机昵称
            userDao.save(user);
            //-------------------------------
            userCode = user.getCode();
            if (user.getUserId() != null) {
                code = ResultEnum.REGISTER_SUCCESS.getCode();
                msg = ResultEnum.REGISTER_SUCCESS.getMessage();
            } else {
                code = ResultEnum.REGISTER_FAILED.getCode();
                msg = ResultEnum.REGISTER_FAILED.getMessage();
            }
        }
        map.put("code", code);
        map.put("msg", msg);
        map.put("userCode", userCode);
        return map;
    }

    @Override
    public R logout() {
        //获取SecurityContextHolder中的用户名
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        JwtUser jwtUser = (JwtUser) authentication.getPrincipal();
        String username = jwtUser.getUsername();
        //删除redis中的值
        redisCache.deleteObject("login" + username);
        return R.ok().resultEnum(ResultEnum.USER_LOGOUT_SUCCESS);
    }

//    这里用userId username
    @Override
    public User getUserInfo(String username) {
        return userDao.findUserByUserId(username);
    }

    @Override
    public R updateUserFace(String url, String id) {
        User user = userDao.findUserByUserId(id);
//        ----------------------------------------
        System.out.println(user.getUserId());
        user.setAvatar(url);
        mongoTemplate.save(user);
        return R.ok().data("url",url);
    }

    @Override
    public List<User> getUserList() {
        return userDao.findAll();
    }

    @Override
    public List<User> searchUser(SearchRequestVo requestVo) {
        Query query = new Query();
        Criteria criteria1 = Criteria.where("code").regex(Pattern.compile("^.*" + requestVo.getSearchContent() + ".*$", Pattern.CASE_INSENSITIVE));
        Criteria criteria2 = Criteria.where("username").regex(Pattern.compile("^.*" + requestVo.getSearchContent() + ".*$", Pattern.CASE_INSENSITIVE));
        Criteria criteria = new Criteria();
        criteria.orOperator(criteria2,criteria1);
        query.addCriteria(
                criteria
        ).with(Sort.by(Sort.Direction.DESC,"_id"));
        return mongoTemplate.find(query,User.class);
    }

    @Override
    public void changeUserStatus(String uid, Integer status) {
        Update update = new Update();
        update.set("status", status);
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(uid)));
        mongoTemplate.findAndModify(query, update, User.class);
    }

    @Override
    public List<User> getUsersBySignUpTime(String lt, String rt) {
        Query query = new Query();
        query.addCriteria(Criteria.where("signUpTime").gte(DateUtil.parseDate(lt, DateUtil.yyyy_MM))
                .lte(DateUtil.parseDate(rt, DateUtil.yyyy_MM)));
        return mongoTemplate.find(query, User.class);
    }

    @Override
    public User getUserInfoById(String uid) {
        return userDao.findById(new ObjectId(uid)).orElse(null);
    }

    @Override
    public void addNewFenZu(NewFenZuRequestVo requestVo) {
        User userInfo = userDao.findById(new ObjectId(requestVo.getUserId())).orElse(null);
        Map<String, ArrayList<String>> friendFenZuMap = userInfo.getFriendFenZu();
        if (!friendFenZuMap.containsKey(requestVo.getFenZuName())) {
            friendFenZuMap.put(requestVo.getFenZuName(), new ArrayList<>());
            Update update = new Update();
            update.set("friendFenZu", friendFenZuMap);
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(new ObjectId(requestVo.getUserId())));
            mongoTemplate.findAndModify(query, update, User.class);
        }
    }

    @Override
    public void modifyFriendFenZu(ModifyFriendFenZuRequestVo requestVo) {
        User userInfo = getUserInfo(requestVo.getUserId());
        boolean flag = false;
        Map<String, ArrayList<String>> friendFenZuMap = userInfo.getFriendFenZu();
        for (Map.Entry<String, ArrayList<String>> item : friendFenZuMap.entrySet()) {
            Iterator<String> iterator = item.getValue().iterator();
            while (iterator.hasNext()) {
                if (iterator.next().equals(requestVo.getFriendId())) {
                    //原来已经在某个分组就从中去掉
                    if (!item.getKey().equals(requestVo.getNewFenZuName())) iterator.remove();
                    flag = true;//没必要再循环了
                    break;
                }
            }
            if (flag) break;
        }
        friendFenZuMap.get(requestVo.getNewFenZuName()).add(requestVo.getFriendId());
        //更新用户信息
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(requestVo.getUserId())));
        Update update = new Update();
        update.set("friendFenZu", friendFenZuMap);
        mongoTemplate.findAndModify(query, update, User.class);
    }

    @Override
    public void deleteFenZu(DelFenZuRequestVo requestVo) {
        User userInfo = getUserInfo(requestVo.getUserId());
        Map<String, ArrayList<String>> friendFenZuMap = userInfo.getFriendFenZu();
        friendFenZuMap.remove(requestVo.getFenZuName());
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(requestVo.getUserId())));
        Update update = new Update();
        update.set("friendFenZu", friendFenZuMap);
        mongoTemplate.findAndModify(query, update, User.class);
    }

    @Override
    public void editFenZu(EditFenZuRequestVo requestVo) {
        User userInfo = getUserInfo(requestVo.getUserId());
        Map<String, ArrayList<String>> friendFenZuMap = userInfo.getFriendFenZu();
        ArrayList<String> oldFenZuUsers = friendFenZuMap.get(requestVo.getOldFenZu());
        if (oldFenZuUsers == null) {
            oldFenZuUsers = new ArrayList<>();
        }
        friendFenZuMap.remove(requestVo.getOldFenZu());
        friendFenZuMap.put(requestVo.getNewFenZu(), oldFenZuUsers);
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(requestVo.getUserId())));
        Update update = new Update();
        update.set("friendFenZu", friendFenZuMap);
        mongoTemplate.findAndModify(query, update, User.class);
    }

    @Override
    public void modifyBeiZhu(ModifyFriendBeiZhuRequestVo requestVo) {
        User userInfo = getUserInfo(requestVo.getUserId());
        Map<String, String> friendBeiZhuMap = userInfo.getFriendBeiZhu();
        friendBeiZhuMap.put(requestVo.getFriendId(), requestVo.getFriendBeiZhuName());
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(requestVo.getUserId())));
        Update update = new Update();
        update.set("friendBeiZhu", friendBeiZhuMap);
        mongoTemplate.findAndModify(query, update, User.class);
    }

    @Override
    public void addFenZuMembers(AddFenZuMemberRequestVo requestVo) {
        List<String> friendIds = requestVo.getFriendIds();
        User userInfo = getUserInfo(requestVo.getUserId());
        Map<String, ArrayList<String>> friendFenZuMap = userInfo.getFriendFenZu();
        for (String friendId : friendIds) {
            friendFenZuMap.get(requestVo.getFenZuName()).add(friendId);
        }
        //更新用户信息
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(requestVo.getUserId())));
        Update update = new Update();
        update.set("friendFenZu", friendFenZuMap);
        mongoTemplate.findAndModify(query, update, User.class);
    }
}
