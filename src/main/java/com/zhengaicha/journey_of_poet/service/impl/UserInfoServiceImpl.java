package com.zhengaicha.journey_of_poet.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhengaicha.journey_of_poet.constants.RewardType;
import com.zhengaicha.journey_of_poet.dto.Result;
import com.zhengaicha.journey_of_poet.dto.UserDTO;
import com.zhengaicha.journey_of_poet.entity.User;
import com.zhengaicha.journey_of_poet.entity.UserInfo;
import com.zhengaicha.journey_of_poet.mapper.UserInfoMapper;
import com.zhengaicha.journey_of_poet.service.UserInfoService;
import com.zhengaicha.journey_of_poet.utils.UserHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    /**
     * 创建userInfo对象并保存
     */
    @Override
    public void init(User user) {
        UserInfo userInfo = new UserInfo(user.getUid());
        this.save(userInfo);
    }

    @Override
    @Transactional
    public Result getReward(String type) {
        UserDTO user = UserHolder.getUser();
        UserInfo one = lambdaQuery().eq(UserInfo::getUid, user.getUid()).one();
        //背诵成功
        if(RewardType.RECITE_TYPE.equals(type)){
            one.setMoney(one.getMoney() + 10);
            updateById(one);
            return Result.success("交子：+10");
        }
        // 厨房挑战成功
        else if (RewardType.KITCHEN_TYPE.equals(type)) {
            one.setMoney(one.getMoney() + 10);
            updateById(one);
            return Result.success("交子：+10");
        }
        return Result.error("类型参数错误");
    }

    @Override
    public UserInfo getOne(Integer uid) {
        return lambdaQuery().eq(UserInfo::getUid,uid).one();
    }
}
