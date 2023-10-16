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

import javax.servlet.http.HttpSession;

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
     *
     * @param gender
     * @param session
     * @return
     */
    public Result modifyGender(Integer gender, HttpSession session) {
        if (gender != null) {
            UserDTO user = (UserDTO) session.getAttribute("user");
            boolean update = this.lambdaUpdate().eq(UserInfo::getUid, user.getUid())
                    .set(UserInfo::getGender, gender).update();
            if (update) {
                return Result.success();
            } else return Result.error("修改失败");
        } else return Result.error("请先选择选项");
    }


    /**
     * 修改个性签名
     *
     * @param signature
     * @param session
     * @return
     */
    public Result modifySignature(String signature, HttpSession session) {
        if (signature != null) {
            UserDTO user = (UserDTO) session.getAttribute("user");
            boolean update = this.lambdaUpdate().eq(UserInfo::getUid, user.getUid())
                    .set(UserInfo::getSignature, signature).update();
            if (update) {
                return Result.success();
            } else return Result.error("修改失败");
        } else return Result.error("请先输入文字");
    }

    /**
     * 用于主页用户信息展示
     * @return
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
