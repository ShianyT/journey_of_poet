package com.zhengaicha.journey_of_poet.controller;

import com.zhengaicha.journey_of_poet.dto.Result;
import com.zhengaicha.journey_of_poet.dto.UserDTO;
import com.zhengaicha.journey_of_poet.entity.UserInfo;
import com.zhengaicha.journey_of_poet.service.UserInfoService;
import com.zhengaicha.journey_of_poet.utils.UserHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.One;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/user/infos")
@Api(tags = "用户个人详细信息接口")
public class UserInfoController {
    @Autowired
    UserInfoService userInfoService;

    @ApiOperation(value = "奖励接口",notes = "奖励的类型有：背诵recite；厨房kitchen")
    @GetMapping("/reward/{type}")
    public Result getReward(@ApiParam("奖励的类型") @PathVariable String type){
        return userInfoService.getReward(type);
    }


    @ApiOperation(value = "获取用户详情信息")
    @GetMapping
    public Result getUserInfo(){
        UserDTO user = UserHolder.getUser();
        if(Objects.isNull(user)){
            return Result.error("出错啦，请登录");
        }
        UserInfo one = userInfoService.getOne(user.getUid());
        return Result.success(one);
    }

}
