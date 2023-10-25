package com.zhengaicha.journey_of_poet.controller;

import com.zhengaicha.journey_of_poet.dto.Result;
import com.zhengaicha.journey_of_poet.entity.PostSubComment;
import com.zhengaicha.journey_of_poet.service.PostSubCommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts/comments/sub")
@Api(tags = "帖子子评论相关接口")
public class PostSubCommentController {

    @Autowired
    private PostSubCommentService postSubCommentService;

    @ApiOperation(value = "保存子评论",notes = "commentedUid为被评论的用户uid")
    @PostMapping("/save")
    public Result savePostSubComment(@ApiParam(value = "传入commentId,commentedUid,content") @RequestBody PostSubComment postSubComment){
        return postSubCommentService.savePostSubComment(postSubComment);
    }

    @ApiOperation(value = "删除子评论")
    @DeleteMapping("/{id}")
    public Result deletePostSubComment(@ApiParam(value = "子评论id") @PathVariable Integer id){
        return postSubCommentService.deletePostSubComment(id);
    }


    @ApiOperation(value = "获取子评论",notes = "一次10条")
    @GetMapping("/{commentId}/{currentPage}")
    public Result getPostSubComment(@ApiParam(value = "主表评论id") @PathVariable Integer commentId
            ,@ApiParam(value = "当前子评论页码") @PathVariable Integer currentPage){
        return postSubCommentService.getPostSubComment(commentId,currentPage);
    }

}
