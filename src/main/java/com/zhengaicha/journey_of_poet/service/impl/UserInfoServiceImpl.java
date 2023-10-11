package com.zhengaicha.journey_of_poet.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhengaicha.journey_of_poet.entity.User;
import com.zhengaicha.journey_of_poet.entity.UserInfo;
import com.zhengaicha.journey_of_poet.mapper.UserInfoMapper;
import com.zhengaicha.journey_of_poet.service.UserInfoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {
    // 固定初始头像路径
    @Value("${UserInfo.avatar}")
    String avatar;
    @Override
    public void init(User user) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUid(user.getUid());
        userInfo.setMail(user.getMail());
        userInfo.setNickname("用户_" + user.getUid());
        userInfo.setAvatar(avatar);
        this.save(userInfo);
    }
}
