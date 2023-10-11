package com.zhengaicha.journey_of_poet.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhengaicha.journey_of_poet.entity.User;

public interface UserService extends IService<User> {

    /**
     * 发送验证码
     * @param mail
     * @return
     */
    public boolean sendCode(String mail);
}
