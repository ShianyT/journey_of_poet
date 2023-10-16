package com.zhengaicha.journey_of_poet.controller;

import com.zhengaicha.journey_of_poet.dto.LoginDTO;
import com.zhengaicha.journey_of_poet.dto.Result;
import com.zhengaicha.journey_of_poet.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


@RestController
@RequestMapping("/users")
@CrossOrigin
@Api(tags = "登陆注册，以及获取和修改用户信息接口")
public class UserController {
    @Autowired
    private UserService userService;


    @ApiOperation(value = "发送验证码", notes = "验证码保留三分钟失效")
    @PostMapping("/code")
    public Result sendCode(@ApiParam(name = "mail", value = "用户邮箱") @RequestBody Map<String, String> mail) {
        return userService.sendCode(mail);
    }

    /**
     * 用户注册账号，要求：
     * 1、用户名不可重复
     */
    @ApiOperation(value = "注册用户", notes = "")
    @PostMapping("/create")
    public Result createUser(@ApiParam(name = "用户对象", value = "传入mail,password,code") @RequestBody LoginDTO login) {
        return userService.createUser(login);
    }

    /**
     * 用户登录
     */
    @ApiOperation(value = "登录", notes = "请将token放入名为\"authorization\"的请求头中")
    @PostMapping("/login")
    public Result login(@ApiParam(name = "用户对象", value = "传入mail,password") @RequestBody LoginDTO loginUser) {
        return userService.login(loginUser);
    }

    /**
     * 当用户忘记密码时修改密码，通过邮箱发送验证码来修改
     */
    @ApiOperation(value = "用邮箱修改密码", notes = "在登录页用户忘记密码时，使用验证码来修改密码")
    @PostMapping("/modify")
    public Result modifyPasswordByEmail(@ApiParam(name = "用户对象", value = "传入mail,code") @RequestBody LoginDTO loginUser) {
        return userService.modifyPasswordByEmail(loginUser);
    }

    /**
     * 更换邮箱时，比对验证码
     */
    @ApiOperation(value = "更换邮箱", notes = "在个人主页中，用户使用验证码更换邮箱")
    public Result verifyCode(@ApiParam(name = "用户对象", value = "传入mail,code") @RequestBody LoginDTO loginUser) {
        return Result.success(null);
    }

    /**
     * 当用户记得旧密码并想修改密码，通过旧密码来修改
     */
    @ApiOperation(value = "用密码修改密码", notes = "在个人主页中，用户使用旧密码修改密码")
    @PostMapping("/modify/pwd")
    public Result modifyPasswordByOldPassword(@ApiParam(name = "密码集合", value = "传入oldPassword,newPassword") @RequestBody Map<String,String> passwords) {
        return userService.modifyPasswordByOldPassword(passwords);
    }

    /**
     * 更换新邮箱
     */
    @ApiOperation(value = "更换邮箱", notes = "在个人主页中，用户使用验证码更换邮箱")
    @PostMapping("/modify/mail")
    public Result modifyMail(@ApiParam(name = "用户对象", value = "传入mail,password") @RequestBody LoginDTO newMailUser) {
        return userService.modifyMail(newMailUser);
    }

    /**
     * 上传头像
     */
    @ApiOperation(value = "上传头像", notes = "在个人主页中，用户上传头像")
    @PostMapping("/modify/icon")
    public Result uploadAvatar(@ApiParam(name = "文件对象", value = "") @RequestBody MultipartFile multipartFile) {
        return userService.uploadAvatar(multipartFile);
    }

    @ApiOperation(value = "修改昵称", notes = "在个人主页中，用户修改昵称")
    @PostMapping("/modify/nickname")
    public Result modifyNickname(@ApiParam(name = "newNickname", value = "新昵称") @RequestParam String newNickname) {
        return userService.modifyNickname(newNickname);
    }

}
