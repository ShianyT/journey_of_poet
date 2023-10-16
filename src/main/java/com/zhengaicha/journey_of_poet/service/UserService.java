package com.zhengaicha.journey_of_poet.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhengaicha.journey_of_poet.dto.LoginDTO;
import com.zhengaicha.journey_of_poet.dto.Result;
import com.zhengaicha.journey_of_poet.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface UserService extends IService<User> {

    /**
     * 发送验证码
     * @param mail
     * @return
     */
    public Result sendCode(Map<String,String> mail);

    public Result createUser(LoginDTO loginUser);

    public Result login(LoginDTO loginUser);

    public Result modifyMail(LoginDTO newMailUser);

    public Result modifyPasswordByEmail(LoginDTO user);

    public Result modifyPasswordByOldPassword(Map<String, String> passwords);

    public Result uploadAvatar(MultipartFile multipartFile);

    public Result modifyNickname(String newNickname);

}
