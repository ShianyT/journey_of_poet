package com.zhengaicha.journey_of_poet.controller;

import com.zhengaicha.journey_of_poet.dto.Result;
import com.zhengaicha.journey_of_poet.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Slf4j
@RestController
@RequestMapping("/user/infos")
@Api(tags = "用户个人详细信息接口")
public class UserInfoController {
    @Autowired
    UserInfoService userInfoService;

    @ApiOperation(value = "修改性别", notes = "")
    @PostMapping("/modify/gender")
    public Result modifyGender(@ApiParam(name = "gender", value = "-1为未知（默认），0为女，1为男") @RequestBody Integer gender, HttpSession session) {
        return userInfoService.modifyGender(gender, session);
    }

    @ApiOperation(value = "修改个性签名", notes = "")
    @PostMapping("/modify/signature")
    public Result modifySignature(@ApiParam(name = "signature", value = "个性签名") @RequestBody String signature, HttpSession session) {
        return userInfoService.modifySignature(signature, session);
    }

    @ApiOperation(value = "展示用户信息" ,notes = "在个人主页，展示用户详细信息; 头像请在地址前加上\"http://ip:端口号/images/icon/\"")
    @GetMapping("/show")
    public Result showUser(){
        return userInfoService.showUser();
    }
}
