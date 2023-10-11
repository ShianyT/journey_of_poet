package com.zhengaicha.journey_of_poet.controller;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhengaicha.journey_of_poet.common.Result;
import com.zhengaicha.journey_of_poet.entity.User;
import com.zhengaicha.journey_of_poet.service.UserInfoService;
import com.zhengaicha.journey_of_poet.service.UserService;
import com.zhengaicha.journey_of_poet.utils.RegexUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import net.sf.jsqlparser.util.validation.metadata.NamedObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static net.sf.jsqlparser.util.validation.metadata.NamedObject.user;


@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserInfoService userInfoService;

    /**
     * 给未注册过的邮箱账号发送激活码（用于用户注册邮箱）
     */
    @PostMapping("/code")
    public Result<User> sendCodeByNotExist(@RequestBody User user) {
        // 1、验证邮箱是否有效
        if (RegexUtils.isEmailValid(user.getMail())) {
            // 2、检查数据库是否有该邮箱
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getMail, user.getMail());
            User user1 = userService.getOne(queryWrapper);
            if (user1 == null) {
                // 3、发送邮件
                if (userService.sendCode(user.getMail())) {
                    return Result.success(null);
                } else return Result.error("邮件发送失败");
            } else return Result.error("该邮箱已存在");
        } else return Result.error("该邮箱无效");
    }

    /**
     * 给以注册过的邮箱账号发送激活码（用于修改密码或更换邮箱）
     */
    @GetMapping("/mail/code")
    public Result<User> sendCodeByExist(HttpSession session) {
        User user = (User) session.getAttribute("user");
        // 1、验证邮箱是否有效
        if (RegexUtils.isEmailValid(user.getMail())) {
            // 2、检查数据库是否有该邮箱
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getMail, user.getMail());
            User user1 = userService.getOne(queryWrapper);
            if (user1 != null) {
                // 3、发送邮件
                if (userService.sendCode(user.getMail())) {
                    return Result.success(null);
                } else return Result.error("邮件发送失败");
            } else return Result.error("该邮箱账号不存在");
        } else return Result.error("该邮箱无效");
    }

    /**
     * 用户注册账号，要求：
     * 1、用户名不可重复
     */
    @PostMapping("/sign-up")
    public Result<User> signUp(@RequestBody User user) {
        // 1、密码加密
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        User user1;
        int uid;
        // 2、获得非重复uid
        do {
            uid = RandomUtil.getRandom().nextInt(100000000, 999999999);
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUid, uid);
            user1 = userService.getOne(queryWrapper);
        } while (user1 != null);
        user.setUid(uid);
        // 3、保存新用户信息
        userService.save(user);
        // 4、初始化该用户信息
        userInfoService.init(user);
        return Result.success(user);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<User> login(HttpSession session, @RequestBody User loginUser) {
        // 1、验证邮箱是否有效
        if (RegexUtils.isEmailValid(loginUser.getMail())) {
            // 2、检查数据库是否有该邮箱
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getMail, loginUser.getMail());
            User user = userService.getOne(queryWrapper);
            if (user != null) {
                // 3、比对密码
                loginUser.setPassword(DigestUtils.md5DigestAsHex(loginUser.getPassword().getBytes()));
                if (user.getPassword().equals(loginUser.getPassword())) {
                    session.setAttribute("user", user);
                    return Result.success(user);
                } else return Result.error("密码错误");
            } else return Result.error("该邮箱账号不存在");
        } else return Result.error("该邮箱无效");
    }

    /**
     * 当用户忘记密码时修改密码，通过邮箱发送验证码来修改
     */
    @PostMapping("/modify")
    public Result<User> modifyPasswordByEmail(@RequestBody User user) {
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getMail, user.getMail()).set(User::getPassword, user.getPassword());
        userService.update(updateWrapper);
        return Result.success(user);
    }

    /**
     * 当用户记得旧密码并想修改密码，通过旧密码来修改
     */
    @PostMapping("/pwd/modify")
    public Result<User> modifyPasswordByOldPassword(@RequestBody Map passwords,HttpSession session) {
        String oldPassword = (String) passwords.get("oldPassword");
        String newPassword = (String) passwords.get("newPassword");
        User user = (User) session.getAttribute("user");
        if (user.getPassword().equals(DigestUtils.md5DigestAsHex(oldPassword.getBytes()))) {
            LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<User>()
                    .eq(User::getMail, user.getMail())
                    .set(User::getPassword, DigestUtils.md5DigestAsHex(newPassword.getBytes()));
            if (userService.update(updateWrapper)) {
                session.removeAttribute("user");
                return Result.success(null);
            } else return Result.error("密码修改失败");
        } else return Result.error("旧密码错误");
    }

    /**
     * 更换邮箱时，比对验证码
     */
    @PostMapping("/mail/next")
    public Result<User> verifyCode(@RequestBody User user) {
        return Result.success(null);
    }

    /**
     * 更换新邮箱
     */
    @PostMapping("/mail/new")
    public Result<User> modifyMail( @RequestBody User newMailUser,HttpSession session) {
        User oldMailUser = (User) session.getAttribute("user");
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getMail, oldMailUser.getMail()).set(User::getMail, newMailUser.getMail());
        userService.update(updateWrapper);
        return Result.success(newMailUser);
    }
}
