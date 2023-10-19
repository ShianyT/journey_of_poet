package com.zhengaicha.journey_of_poet.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhengaicha.journey_of_poet.dto.Result;
import com.zhengaicha.journey_of_poet.entity.PostSubComment;

public interface PostSubCommentService extends IService<PostSubComment> {
    public Result savePostSubComment(PostSubComment postSubComment);

    public Result deletePostSubComment(Integer id);

    public Result getPostSubComment(Integer commentId, Integer currentPage);

    public PostSubComment getOnePostSubComment(int commentId);

    public long getPostSubCommentNum(int commentId);
}
