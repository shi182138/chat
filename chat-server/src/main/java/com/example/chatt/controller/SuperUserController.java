package com.example.chatt.controller;

import com.example.chatt.annotation.common.R;
import com.example.chatt.annotation.common.ResultEnum;
import com.example.chatt.pojo.SuperUser;
import com.example.chatt.service.SuperUserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SuperUserController {
    @Autowired
    private SuperUserService superUserService;

    @ApiOperation(value = "管理员登录")
    @PostMapping("/superLogin")
    public R superUserLogin(@RequestBody SuperUser superUser) {
        Map<String, Object> resMap = superUserService.superUserLogin(superUser);
        Integer code = (Integer) resMap.get("code");
        if (code.equals(ResultEnum.LOGIN_SUCCESS.getCode()))
            return R.ok().resultEnum(ResultEnum.LOGIN_SUCCESS)
                    .data("userInfo", resMap.get("userInfo"))
                    .data("token", resMap.get("token"));
        else return R.error().code(code).message((String) resMap.get("msg"));
    }
}
