package com.zhengaicha.journey_of_poet.controller;

import com.zhengaicha.journey_of_poet.dto.Result;
import com.zhengaicha.journey_of_poet.service.GameContentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/games/content")
@Api(tags = "获取游戏文本接口")
public class GameContentController {

    @Autowired
    private GameContentService gameContentService;

    @ApiOperation(value = "获得一整个场景的游戏文本", notes = "get方法，路径后面跟上scene获得该场景的全部文本")
    @GetMapping("/{scene}")
    public Result getContent(@ApiParam(name = "scene",value = "0为旁白，1为对话，2为选项") @PathVariable Integer scene){
       return gameContentService.getContent(scene);
    }
}
