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

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


@RestController
@RequestMapping("/users")
@Api(tags = "登陆注册，以及获取和修改用户信息接口")
public class UserController {
    @Autowired
    private UserService userService;


    @ApiOperation(value = "发送验证码", notes = "验证码保留三分钟失效")
    @PostMapping("/code")
    public Result sendCode(@ApiParam(name = "mail", value = "用户邮箱；不用转成json，直接发送数据") @RequestParam String mail) {
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
    public Result modifyPasswordByEmail(@ApiParam(name = "用户对象", value = "传入mail,code")
                                        @RequestBody LoginDTO loginUser, HttpServletRequest request) {
        return userService.modifyPasswordByEmail(loginUser, request);
    }


    /**
     * 当用户记得旧密码并想修改密码，通过旧密码来修改
     */
    @ApiOperation(value = "用密码修改密码", notes = "在个人主页中，用户使用旧密码修改密码")
    @PostMapping("/modify/pwd")
    public Result modifyPasswordByOldPassword(@ApiParam(name = "密码集合", value = "传入oldPassword,newPassword")
                                              @RequestBody Map<String, String> passwords, HttpServletRequest request) {
        return userService.modifyPasswordByOldPassword(passwords, request);
    }


    /**
     * 上传头像
     */
    @ApiOperation(value = "上传头像", notes = "在个人主页中，用户上传头像")
    @PostMapping("/modify/icon")
    public Result uploadIcon(@ApiParam(name = "file", value = "名字与file一致") @RequestBody MultipartFile file,
                             HttpServletRequest request) {
        return userService.uploadIcon(file, request);
    }

    /**
     * 修改昵称
     */
    @ApiOperation(value = "修改昵称", notes = "在个人主页中，用户修改昵称")
    @PostMapping("/modify/nickname")
    public Result modifyNickname(@ApiParam(name = "newNickname", value = "新昵称") @RequestBody Map<String, String> newNickname,
                                 HttpServletRequest request) {
        return userService.modifyNickname(newNickname.get("newNickname"), request);
    }

    @ApiOperation(value = "修改性别")
    @PostMapping("/modify/gender")
    public Result modifyGender(@ApiParam(name = "gender", value = "-1为未知（默认），0为女，1为男；不用传入json，数据即可")
                               @RequestParam Integer gender) {
        return userService.modifyGender(gender);
    }

    @ApiOperation(value = "修改个性签名")
    @PostMapping("/modify/signature")
    public Result modifySignature(@ApiParam(name = "signature", value = "个性签名；不用传入json，数据即可")
                                  @RequestParam String signature) {
        return userService.modifySignature(signature);
    }

    @ApiOperation(value = "展示用户信息" ,notes = "在个人主页，展示用户详细信息; 头像请在地址前加上\"http://ip:端口号/images/icon/\"")
    @GetMapping("/show")
    public Result showUser(){
        return userService.showUser();
    }
}
