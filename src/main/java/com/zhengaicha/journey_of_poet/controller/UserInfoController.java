package com.zhengaicha.journey_of_poet.controller;

import com.zhengaicha.journey_of_poet.common.Result;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-infos")
@CrossOrigin
public class UserInfoController {

    @PostMapping("/avatar")
    public Result uploadAvatar(){
        return Result.error("功能尚未完成");
    }

    @PostMapping("/nickname")
    public Result modifyNickname(@RequestBody String newNickname){
        return Result.error("功能尚未完成");
    }

    @PostMapping("/gender")
    public Result modifyGender(@RequestBody int gender){
        return Result.error("功能尚未完成");
    }

    @PostMapping("/signature")
    public Result modifySignature(@RequestBody String signature){
        return Result.error("功能尚未完成");
    }

}
