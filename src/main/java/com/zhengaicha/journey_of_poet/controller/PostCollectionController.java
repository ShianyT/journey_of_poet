package com.zhengaicha.journey_of_poet.controller;

import com.zhengaicha.journey_of_poet.dto.Result;
import com.zhengaicha.journey_of_poet.entity.PostCollection;
import com.zhengaicha.journey_of_poet.entity.PostLike;
import com.zhengaicha.journey_of_poet.service.PostCollectionService;
import com.zhengaicha.journey_of_poet.service.PostLikeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts/collections")
@Api(tags = "收藏相关接口")
public class PostCollectionController {

    @Autowired
    private PostCollectionService PostCollectionService;

    @ApiOperation(value = "收藏或取消收藏")
    @PostMapping()
    public Result collection(@ApiParam(value = "传入 postId status") @RequestBody PostCollection postCollection) {
        return PostCollectionService.collection(postCollection);
    }
}
