package com.zhengaicha.journey_of_poet.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhengaicha.journey_of_poet.dto.Result;
import com.zhengaicha.journey_of_poet.dto.UserDTO;
import com.zhengaicha.journey_of_poet.entity.User;
import com.zhengaicha.journey_of_poet.entity.UserInfo;
import com.zhengaicha.journey_of_poet.mapper.UserInfoMapper;
import com.zhengaicha.journey_of_poet.service.UserInfoService;
import com.zhengaicha.journey_of_poet.utils.UserHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {
    // @Autowired
    // private UserService userService;

    /**
     * 创建userInfo对象并保存
     *
     * @param user
     */
    @Override
    public void init(User user) {
        UserInfo userInfo = new UserInfo(user.getUid());
        this.save(userInfo);
    }

    /**
     * 修改性别
     */
    public Result modifyGender(Integer gender) {
        UserDTO user = UserHolder.getUser();
        if (Objects.isNull(user)) {
            return Result.error("出错啦！请登录");
        }

        if (Objects.isNull(gender)) {
            return Result.error("请先选择选项");
        }

        boolean update = this.lambdaUpdate().eq(UserInfo::getUid, user.getUid())
                .set(UserInfo::getGender, gender).update();
        if (update) {
            return Result.success();
        }
        return Result.error("修改失败");
    }


    /**
     * 修改个性签名
     */
    public Result modifySignature(String signature) {

        UserDTO user = UserHolder.getUser();
        if (Objects.isNull(user)) {
            return Result.error("出错啦！请登录");
        }

        boolean update = this.lambdaUpdate().eq(UserInfo::getUid, user.getUid())
                .set(UserInfo::getSignature, signature).update();
        if (update) {
            return Result.success();
        }
        return Result.error("修改失败");

    }

    /**
     * 用于主页用户信息展示
     */
    @Override
    public Result showUser() {
        UserDTO user = UserHolder.getUser();
        UserInfo userInfo = lambdaQuery().eq(UserInfo::getUid, user.getUid()).one();
        userInfo.setNickname(user.getNickname());
        userInfo.setIcon(user.getIcon());
        return Result.success(userInfo);
    }
}
