package com.example.chatt.controller;

import com.example.chatt.annotation.common.R;
import com.example.chatt.pojo.User;
import com.example.chatt.pojo.vo.SystemUserResponseVo;
import com.example.chatt.service.OnlineUserService;
import com.example.chatt.service.SysService;
import com.example.chatt.service.UserService;
import com.example.chatt.utils.FastDFSUtil;
import com.example.chatt.utils.SystemUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.List;

@RestController
public class SysController {
    @Autowired
    private UserService userService;
    @Autowired
    private SysService sysService;
    @Resource
    private OnlineUserService onlineUserService;

    @ApiOperation(value = "获取所有用户信息")
    @GetMapping("/getAllUser")
    public R getAllUser() {
        List<User> userList = userService.getUserList();
        return R.ok().data("userList",userList);
    }

    @ApiOperation(value = "获取系统用户")
    @GetMapping("/getSysUsers")
    public R getSysUser() {
        List<SystemUserResponseVo> systemUsers = sysService.getSysUsers();
        return R.ok().data("systemUsers",systemUsers);
    }

    @ApiOperation(value = "文件上传")
    @PostMapping("/uploadFile")
    public R uploadFile(MultipartFile file) {
        //根据扩展名来设置消息类型：emoji/text/img/file/sys/whiteboard/video/audio
        System.out.println("sysController:文件上传");
        System.out.println(file);
        String[] res = FastDFSUtil.upload(file);
        String filePath = FastDFSUtil.getTrackerUrl() + res[0] + "/" + res[1];
        return R.ok().data("filePath",filePath);
    }

    /**
     * 提供文件下载
     */
//    @ApiOperation(value = "文件下载")
//    @GetMapping("/downloadFile")
//    public void downloadFile(@RequestParam("fileId") String fileId,
//                             @RequestParam("fileName") String fileName,
//                             HttpServletResponse resp) {
//        try {
//            byte[] bytes = FastDFSUtil.downloadFile(fileId);
//            resp.setCharacterEncoding("UTF-8");
//            resp.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
//            ServletOutputStream outputStream = resp.getOutputStream();
//            IOUtils.write(bytes, outputStream);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @ApiOperation(value = "获取Cpu，内存使用率")
    @GetMapping("/sysSituation")
    @ResponseBody
    public R getSysInfo() {
        double cpuUsage = SystemUtil.getSystemCpuLoad();
        double memUsage = SystemUtil.getSystemMemLoad();
        return R.ok().data("cpuUsage", cpuUsage).data("memUsage", memUsage);
    }

    /**
     * 更改用户状态
     */
    @ApiOperation(value = "更改用户状态")
    @GetMapping("/changeUserStatus")
    @ResponseBody
    public R changeUserStatus(String uid, Integer status) {
        userService.changeUserStatus(uid, status);
        return R.ok();
    }

    /**
     * 获取在线用户个数
     */
    @ApiOperation(value = "获取在线用户个数")
    @GetMapping("/countOnlineUser")
    @ResponseBody
    public R getOnlineUserNums() {
        int onlineUserCount = onlineUserService.countOnlineUser();
        return R.ok().data("onlineUserCount", onlineUserCount);
    }

    /**
     * 根据注册时间获取用户
     */
    @ApiOperation(value = "根据注册时间获取用户")
    @GetMapping("/getUsersBySignUpTime")
    @ResponseBody
    public R getUsersBySignUpTime(String lt, String rt) {
        List<User> userList = userService.getUsersBySignUpTime(lt, rt);
        return R.ok().data("userList", userList);
    }
}
