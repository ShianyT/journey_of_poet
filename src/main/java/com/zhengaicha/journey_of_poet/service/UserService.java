package com.zhengaicha.journey_of_poet.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhengaicha.journey_of_poet.dto.LoginDTO;
import com.zhengaicha.journey_of_poet.dto.Result;
import com.zhengaicha.journey_of_poet.entity.User;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface UserService extends IService<User> {

    /**
     * 发送验证码
     */
    public Result sendCode(String mail);

    public Result createUser(LoginDTO loginUser);

    public Result login(LoginDTO loginUser);

    public Result modifyPasswordByEmail(LoginDTO user, HttpServletRequest request);

    public Result modifyPasswordByOldPassword(Map<String, String> passwords, HttpServletRequest request);

    public Result uploadIcon(MultipartFile multipartFile, HttpServletRequest result);

    public Result modifyNickname(String newNickname, HttpServletRequest request);

    public Result modifyGender(Integer gender);

    public Result modifySignature(String signature);

    public Result showUser();

    public User getOne(Integer uid);
}
