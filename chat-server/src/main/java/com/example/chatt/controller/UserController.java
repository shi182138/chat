package com.example.chatt.controller;
import com.example.chatt.annotation.common.R;
import com.example.chatt.annotation.common.ResultEnum;
import com.example.chatt.pojo.User;
import com.example.chatt.pojo.vo.*;
import com.example.chatt.service.UserService;
import com.example.chatt.utils.FastDFSUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     */
    @ApiOperation(value="用户注册")
    @PostMapping(value = "user/register")
    public R register(@RequestBody RegisterRequestVo registerRequestVo) {
        Map<String, Object> resMap = userService.register(registerRequestVo);
        Integer code = (Integer) resMap.get("code");
        if (code.equals(ResultEnum.REGISTER_SUCCESS.getCode()))
            return R.ok().resultEnum(ResultEnum.REGISTER_SUCCESS).data("userCode", resMap.get("userCode"));
        else return R.error().code(code).message((String) resMap.get("msg"));
    }

    /**
     *用户退出
     */
    @ApiOperation(value="用户退出")
    @RequestMapping("logout")
    public R logout() {
        return userService.logout();
    }

    /**
     * 获取用户信息
     */
//    @RequestMapping("/getUserInfoId")
//    public R getUserInfo(String userId) {
//        User userInfo = userService.getUserInfo(userId);
//        return R.ok().data("userInfo",userInfo);
//    }

    /**
     * 获取用户信息Xxx
     */
//    @GetMapping("getUserInfo")
//    public Map getUserInfo(Principal principal) {
//        System.out.println("获取用户信息");
//        System.out.println(principal);
//        if (principal == null) {
//            return null;
//        }
//        String username = principal.getName();
//        User userInfo = userService.getUserInfo(username);
//        userInfo.setPassword(null);
//        Map<String, Object> map = new HashMap<>();
//        map.put("userInfo",userInfo);
//        return map;
//    }

    /**
     * 获取用户信息Xxx
     */
    @GetMapping("getUserInfo")
    public Map getUserInfo(Principal principal) {
        System.out.println("获取用户信息");
        System.out.println(principal);
        if (principal == null) {
            return null;
        }
        System.out.println(principal.getName());
        String userId = principal.getName();
        User userInfo = userService.getUserInfo(userId);
        userInfo.setPassword(null);
        Map<String, Object> map = new HashMap<>();
        map.put("userInfo",userInfo);
        return map;
    }

    /**
     * 更新用户头像
     */
    @ApiOperation(value = "更新用户头像")
    @PostMapping("updateUserFace")
    public R updateUserface(MultipartFile file, String id) {
        String[] filePath = FastDFSUtil.upload(file);
        String url = FastDFSUtil.getTrackerUrl() + filePath[0] + "/" + filePath[1];
        return userService.updateUserFace(url,id);
    }
    @ApiOperation(value = "搜索好友")
    @PostMapping("/searchUserList")
    public R searchUser(@RequestBody SearchRequestVo requestVo) {
//        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<User> userList = userService.searchUser(requestVo);
        return R.ok().data("userList",userList);
    }

    /**
     * 获取用户详情
     */
    @ApiOperation(value = "根据用户id获取用户详情")
    @GetMapping("/getUserInfoById")
    public R getUserInfo(String uid) {
        User userInfo = userService.getUserInfoById(uid);
        return R.ok().data("userInfo", userInfo);
    }

    /**
     * 添加新的分组
     */
    @ApiOperation(value = "添加新的分组")
    @PostMapping("/addFenZu")
    public R addNewFenZu(@RequestBody NewFenZuRequestVo requestVo) {
        userService.addNewFenZu(requestVo);
        return R.ok().message("添加新分组成功");
    }

    @ApiOperation(value = "添加分组成员")
    @PostMapping("/addFenZuMembers")
    public R addFenZuMembers(@RequestBody AddFenZuMemberRequestVo requestVo) {
        userService.addFenZuMembers(requestVo);
        return R.ok();
    }


    /**
     * 修改好友分组
     */
    @ApiOperation(value = "修改好友分组分组")
    @PostMapping("/modifyFriendFenZu")
    public R modifyFriendFenZu(@RequestBody ModifyFriendFenZuRequestVo requestVo) {
        // System.out.println("修改分组的请求参数为：" + requestVo);
        userService.modifyFriendFenZu(requestVo);
        return R.ok().message("修改分组成功！");
    }

    /**
     * 删除分组
     */
    @ApiOperation(value = "删除分组")
    @DeleteMapping("/delFenZu")
    public R deleteFenZu(@RequestBody DelFenZuRequestVo requestVo) {
        userService.deleteFenZu(requestVo);
        return R.ok().message("删除成功！");
    }

    /**
     * 更新分组名（编辑分组）
     */
    @ApiOperation(value = "更新分组名（编辑分组）")
    @PostMapping("/editFenZu")
    public R editFenZu(@RequestBody EditFenZuRequestVo requestVo) {
        userService.editFenZu(requestVo);
        return R.ok();
    }

    /**
     * 修改好友备注信息
     */
    @PostMapping("/modifyFriendBeiZhu")
    public R modifyFriendBeiZhu(@RequestBody ModifyFriendBeiZhuRequestVo requestVo) {
        userService.modifyBeiZhu(requestVo);
        return R.ok().message("修改备注成功！");
    }
}
