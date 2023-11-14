package com.zhengaicha.journey_of_poet.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhengaicha.journey_of_poet.dto.Result;
import com.zhengaicha.journey_of_poet.entity.User;
import com.zhengaicha.journey_of_poet.entity.UserInfo;

import javax.servlet.http.HttpSession;

public interface UserInfoService extends IService<UserInfo> {
    /**
     * 初始化UserInfo对象，并将其存进数据库
     * @param user
     */
    void init(User user);

    public Result getReward(String type);

    public UserInfo getOne(Integer uid);
}
