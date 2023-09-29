package com.zhengaicha.sycamore_street.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhengaicha.sycamore_street.common.Result;
import com.zhengaicha.sycamore_street.entity.User;
import com.zhengaicha.sycamore_street.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     * @param request
     * @param user
     * @return
     */
    @PostMapping("/login")
    public Result<User> login(HttpServletRequest request, @RequestBody User user){
        String password = user.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());// md5密文加密

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername,user.getUsername());
        User userOne = userService.getOne(queryWrapper);

        if(userOne == null){
            return Result.error("登陆失败");
        }
        if(!userOne.getPassword().equals(password)){
            return Result.error("登陆失败");
        }
        request.getSession().setAttribute("User",user.getId());
        return Result.success(user);
    }
}
