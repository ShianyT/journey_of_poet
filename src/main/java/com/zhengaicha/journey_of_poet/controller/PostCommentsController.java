package com.zhengaicha.journey_of_poet.controller;

import com.zhengaicha.journey_of_poet.dto.Result;
import com.zhengaicha.journey_of_poet.entity.PostComment;
import com.zhengaicha.journey_of_poet.service.PostCommentsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts/comments")
@Api(tags = "直接评论在帖子的评论相关接口")
public class PostCommentsController {
    @Autowired
    private PostCommentsService postCommentsService;

    @ApiOperation(value = "保存评论")
    @PostMapping("/save")
    public Result savePostComment(@ApiParam(value = "传入postId,content") @RequestBody PostComment postComment){
        return postCommentsService.savePostComment(postComment);
    }

    @ApiOperation(value = "删除直接评论",notes = "当评论为直接评论在帖子时，选择该接口，只可删除用户自己发布的评论")
    @DeleteMapping("/{id}")
    public Result deletePostComment(@ApiParam(value = "评论id") @PathVariable Integer id){
        return postCommentsService.deletePostComment(id);
    }

    @ApiOperation(value = "获得直接评论",notes = "当用户直接评论帖子时，选择该接口")
    @GetMapping("/{postId}/{currentPage}")
    public Result getPostComment(@ApiParam(value = "帖子id") @PathVariable Integer postId,
                                 @ApiParam(value = "评论的页码，从1开始即可") @PathVariable Integer currentPage){
        return postCommentsService.getPostComment(postId,currentPage);
    }

}
