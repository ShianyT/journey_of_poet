package com.zhengaicha.journey_of_poet.controller;

import com.zhengaicha.journey_of_poet.dto.Result;
import com.zhengaicha.journey_of_poet.entity.PostLike;
import com.zhengaicha.journey_of_poet.service.PostLikeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts/likes")
@Api(tags = "点赞相关接口")
public class PostLikeController {

    @Autowired
    private PostLikeService postLikeService;

    @ApiOperation(value = "点赞或取消点赞")
    @PostMapping()
    public Result like(@ApiParam(value = "传入 postId status") @RequestBody PostLike postLike) {
        return postLikeService.like(postLike);
    }
}
