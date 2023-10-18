package com.zhengaicha.journey_of_poet.controller;

import com.zhengaicha.journey_of_poet.dto.Result;
import com.zhengaicha.journey_of_poet.service.GameMomentsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/games/moments")
@Api(tags = "获取一整个朋友圈的内容接口")
public class GameMomentsController {

    @Autowired
    private GameMomentsService gameMomentsService;

    @GetMapping("/{scene}/{currentPage}")
    @ApiOperation(value = "获取一整个朋友圈内容",notes = "时间只需展示timeDifference结果即可")
    public Result getGameMoments(@ApiParam(value = "当前场景id") @PathVariable Integer scene,
                                 @ApiParam(value = "朋友圈页数，每页5条，从1开始递增即可") @PathVariable Integer currentPage){
        return gameMomentsService.getGameMoments(scene,currentPage);
    }
}
