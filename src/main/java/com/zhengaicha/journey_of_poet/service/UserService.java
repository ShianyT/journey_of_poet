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
     * @param mail
     * @return
     */
    public Result sendCode(Map<String,String> mail);

    public Result createUser(LoginDTO loginUser);

    public Result login(LoginDTO loginUser);

    public Result modifyMail(LoginDTO newMailUser,HttpServletRequest request);

    public Result modifyPasswordByEmail(LoginDTO user,HttpServletRequest request);

    public Result modifyPasswordByOldPassword(Map<String, String> passwords,HttpServletRequest request);

    public Result uploadIcon(MultipartFile multipartFile, HttpServletRequest result);

    public Result modifyNickname(String newNickname, HttpServletRequest request);

}
