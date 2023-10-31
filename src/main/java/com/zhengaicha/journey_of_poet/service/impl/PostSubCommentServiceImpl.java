package com.zhengaicha.journey_of_poet.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhengaicha.journey_of_poet.dto.Result;
import com.zhengaicha.journey_of_poet.dto.UserDTO;
import com.zhengaicha.journey_of_poet.entity.Post;
import com.zhengaicha.journey_of_poet.entity.PostComment;
import com.zhengaicha.journey_of_poet.entity.PostSubComment;
import com.zhengaicha.journey_of_poet.entity.User;
import com.zhengaicha.journey_of_poet.mapper.PostSubCommentMapper;
import com.zhengaicha.journey_of_poet.service.PostCommentsService;
import com.zhengaicha.journey_of_poet.service.PostService;
import com.zhengaicha.journey_of_poet.service.PostSubCommentService;
import com.zhengaicha.journey_of_poet.service.UserService;
import com.zhengaicha.journey_of_poet.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class PostSubCommentServiceImpl extends ServiceImpl<PostSubCommentMapper, PostSubComment>
        implements PostSubCommentService {

    @Autowired
    private PostCommentsService postCommentsService;

    @Autowired
    private UserService userService;


    @Override
    public Result savePostSubComment(PostSubComment postSubComment) {
        UserDTO user = UserHolder.getUser();
        if (Objects.isNull(user)) {
            return Result.error("出错啦！请登录");
        }

        if (Objects.isNull(postSubComment.getCommentedUid())
                || Objects.isNull(postSubComment.getContent())
                || Objects.isNull(postSubComment.getCommentId())) {
            return Result.error("传入参数不足");
        }

        if (userService.isUserNotExist(postSubComment.getCommentedUid())) {
            return Result.error("被评论用户不存在");
        }
        if (postCommentsService.isPostCommentNotExist(postSubComment.getCommentId())) {
            return Result.error("主表评论不存在");
        }

        postSubComment.setUid(user.getUid());
        if (save(postSubComment)) {
            postCommentsService.addPostCommentNum(postSubComment.getCommentId());
            return Result.success();
        }
        return Result.error("服务器错误，评论保存失败");
    }

    @Override
    public Result deletePostSubComment(Integer id) {
        UserDTO user = UserHolder.getUser();
        if (Objects.isNull(user)) {
            return Result.error("出错啦！请登录");
        }

        PostSubComment postSubComment = lambdaQuery().eq(PostSubComment::getId, id).one();
        if (Objects.isNull(postSubComment)) {
            return Result.error("评论不存在");
        }

        if (!Objects.equals(user.getUid(), postSubComment.getUid())) {
            return Result.error("该评论为其他用户发布");
        }

        if (removeById(id)) {
            return Result.success();
        }
        log.error("子评论删除失败");
        return Result.error("删除失败");
    }

    @Override
    public Result getPostSubComment(Integer commentId, Integer currentPage) {
        if (postCommentsService.isPostCommentNotExist(commentId)) {
            return Result.error("主表评论不存在");
        }
        if (currentPage < 1)
            return Result.error("页码错误");

        List<PostSubComment> postSubComments = lambdaQuery()
                .eq(PostSubComment::getCommentId, commentId)
                .orderByDesc(PostSubComment::getId)
                .page(new Page<>(currentPage, 10))
                .getRecords();

        if (postSubComments.isEmpty()) {
            return Result.error("评论已经到最尾");
        }

        for (PostSubComment postSubComment : postSubComments) {
            User user = userService.getOne(postSubComment.getUid());
            postSubComment.setNickname(user.getNickname());
            postSubComment.setIcon(user.getIcon());
            user = userService.getOne(postSubComment.getCommentedUid());
            postSubComment.setCommentedNickname(user.getNickname());
        }
        return Result.success(postSubComments);
    }

    /**
     * 获取一条子评论
     */
    public PostSubComment getOnePostSubComment(int commentId) {
        PostSubComment postSubComment = lambdaQuery().eq(PostSubComment::getCommentId, commentId).orderByDesc(PostSubComment::getId)
                .last("limit 1").one();
        if (Objects.isNull(postSubComment)) {
            return null;
        }

        User user = userService.getOne(postSubComment.getUid());
        postSubComment.setNickname(user.getNickname());
        postSubComment.setIcon(user.getIcon());
        user = userService.getOne(postSubComment.getCommentedUid());
        postSubComment.setCommentedNickname(user.getNickname());
        return postSubComment;
    }

    /**
     * 获取子评论数量
     */
    public long getPostSubCommentNum(int commentId) {
        return lambdaQuery().eq(PostSubComment::getCommentId, commentId).count();
    }
}
