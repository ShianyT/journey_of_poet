package com.zhengaicha.journey_of_poet.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhengaicha.journey_of_poet.dto.Result;
import com.zhengaicha.journey_of_poet.dto.UserDTO;
import com.zhengaicha.journey_of_poet.entity.Post;
import com.zhengaicha.journey_of_poet.entity.PostComment;
import com.zhengaicha.journey_of_poet.entity.PostSubComment;
import com.zhengaicha.journey_of_poet.entity.User;
import com.zhengaicha.journey_of_poet.mapper.PostCommentsMapper;
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
public class PostCommentsServiceImpl extends ServiceImpl<PostCommentsMapper, PostComment>
        implements PostCommentsService {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private PostSubCommentService postSubCommentService;

    @Override
    public Result savePostComment(PostComment postComment) {
        UserDTO user = UserHolder.getUser();
        if (Objects.isNull(user))
            return Result.error("出错啦！请登录");

        if (Objects.isNull(postComment.getContent()) || Objects.isNull(postComment.getPostId()))
            return Result.error("传入参数不足");

        Post post = postService.getOnePost(postComment.getPostId());
        if(Objects.isNull(post)){
            return Result.error("帖子不存在");
        }

        // 保存评论
        postComment.setUid(user.getUid());
        if (save(postComment)) {
            if(Objects.isNull(post.getComments()))
                post.setComments(1);
            post.setComments(post.getComments() + 1);
            postService.updateById(post);
            return Result.success();
        }
        log.error("评论保存失败");
        return Result.error("评论保存失败");
    }

    @Override
    public Result deletePostComment(Integer id) {

        UserDTO user = UserHolder.getUser();
        if(Objects.isNull(user)){
            return Result.error("出错啦！请登录");
        }

        PostComment postComment = lambdaQuery().eq(PostComment::getId, id).one();
        if (Objects.isNull(postComment))
            return Result.error("评论不存在");

        if(!Objects.equals(user.getUid(),postComment.getUid())){
            return Result.error("该评论为其他用户发布");
        }

        if (removeById(id))
            return Result.success();
        log.error("评论删除失败");
        return Result.error("删除失败");
    }

    @Override
    public Result getPostComment(Integer postId, Integer currentPage) {
        if (!postService.lambdaQuery().eq(Post::getId,postId).exists())
            return Result.error("帖子不存在");
        if(currentPage < 1)
            return Result.error("页码错误");

        //获取直接评论在帖子的评论
        List<PostComment> postComments = lambdaQuery().eq(PostComment::getPostId, postId).orderByDesc(PostComment::getId)
                .page(new Page<>(currentPage,20)).getRecords();

        if(postComments.isEmpty())
            return Result.error("评论已经到最尾");

        //获取用户昵称、头像、子评论、子评论数
        for(PostComment postComment : postComments){
            User user = userService.lambdaQuery().eq(User::getUid, postComment.getUid()).one();
            postComment.setNickname(user.getNickname());
            postComment.setIcon(user.getIcon());
            PostSubComment postSubComment = postSubCommentService.getOnePostSubComment(postComment.getId());
            if(!Objects.isNull(postSubComment)){
                postComment.setPostSubComment(postSubComment);
                User user1 = userService.lambdaQuery().eq(User::getUid, postSubComment.getUid()).one();
                postSubComment.setNickname(user1.getNickname());
                postSubComment.setIcon(user1.getIcon());
                long postSubCommentNum = postSubCommentService.getPostSubCommentNum(postComment.getId());
                postComment.setPostSubCommentNum(postSubCommentNum);
            }
        }

        return Result.success(postComments);
    }

    @Override
    public boolean isPostCommentNotExist(Integer commentId) {
        return !lambdaQuery().eq(PostComment::getId,commentId).exists();
    }

    public void addPostCommentNum(int id){
        PostComment postComment = lambdaQuery().eq(PostComment::getId, id).one();
        Post post = postService.getOnePost(postComment.getPostId());
        if(Objects.isNull(post.getComments()))
            post.setComments(0);
        post.setComments(post.getComments() + 1);
        postService.updateById(post);
    }
}
