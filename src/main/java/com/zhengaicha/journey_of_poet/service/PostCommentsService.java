package com.zhengaicha.journey_of_poet.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhengaicha.journey_of_poet.dto.Result;
import com.zhengaicha.journey_of_poet.entity.PostComment;

public interface PostCommentsService extends IService<PostComment> {
    public Result savePostComment(PostComment postComment);

    public Result deletePostComment(Integer id);

    public Result getPostComment(Integer postId, Integer currentPage);

    public boolean isPostCommentNotExist(Integer commentId);

    public void addPostCommentNum(int id);
}
