package com.zhengaicha.journey_of_poet.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhengaicha.journey_of_poet.dto.Result;
import com.zhengaicha.journey_of_poet.dto.UserDTO;
import com.zhengaicha.journey_of_poet.entity.Post;
import com.zhengaicha.journey_of_poet.entity.PostLike;
import com.zhengaicha.journey_of_poet.mapper.PostLikeMapper;
import com.zhengaicha.journey_of_poet.service.PostLikeService;
import com.zhengaicha.journey_of_poet.service.PostService;
import com.zhengaicha.journey_of_poet.constants.PostStatus;
import com.zhengaicha.journey_of_poet.utils.RedisConstants;
import com.zhengaicha.journey_of_poet.utils.RedisUtils;
import com.zhengaicha.journey_of_poet.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Objects;

import static com.zhengaicha.journey_of_poet.constants.PostStatus.LIKE;

@Service
public class PostLikeServiceImpl extends ServiceImpl<PostLikeMapper, PostLike> implements PostLikeService {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private PostService postService;

    @Override
    public Result like(PostLike postLike) {
        UserDTO user = UserHolder.getUser();
        if (Objects.isNull(user)) {
            return Result.error("出错啦！请登录");
        }
        postLike.setUid(user.getUid());
        // 增加点赞
        if (LIKE.getCode().equals(postLike.getStatus())) {
            return addLike(postLike);
        }
        // 减少点赞
        if (PostStatus.UNLIKE.getCode().equals(postLike.getStatus())) {
            return deleteLike(postLike);
        }
        return Result.error("传参错误");
    }


    public Result addLike(PostLike postLike) {
        // 在redis中查询是否有该条点赞记录
        String likeDetail = redisUtils.getLikeDetail(postLike);
        // 有--->查看点赞记录状态
        if (likeDetail != null) {
            Integer status = RedisConstants.getPostStatus(likeDetail);
            // 状态为0，则改状态为1，点赞量+1
            if (PostStatus.UNLIKE.getCode().equals(status)) {
                if (redisUtils.saveLike(postLike)) {
                    return Result.success();
                } else {
                    log.error("服务器出错，点赞修改失败");
                    return Result.error("修改失败");
                }
            }
            // 状态为1，则报错
            else return Result.error("您已经点赞过了");
        }
        // 没有--->在数据库中查询是否有该点赞记录
        PostLike postLike1 = this.lambdaQuery().eq(PostLike::getPostId, postLike.getPostId())
                .eq(PostLike::getUid, postLike.getUid()).one();
        // 有--->查看记录状态
        if (postLike1 != null) {
            // 状态为0，则改状态为1，并保存在redis中，点赞量+1
            if (PostStatus.UNLIKE.getCode().equals(postLike1.getStatus())) {
                postLike1.setStatus(postLike.getStatus());
                if (redisUtils.saveLike(postLike)) {
                    return Result.success();
                } else {
                    log.error("服务器出错，点赞修改失败");
                    return Result.error("修改失败");
                }
            }
            // 状态为1，则报错
            else return Result.error("您已经点赞过了");
        }
        // 没有--->在redis中增加点赞记录，点赞量+1
        redisUtils.saveLike(postLike);
        return Result.success();
    }

    public Result deleteLike(PostLike postLike) {
        // 在redis中查询是否有该条点赞记录
        String likeDetail = redisUtils.getLikeDetail(postLike);
        // 有--->查看点赞记录状态
        if (likeDetail != null) {
            Integer status = RedisConstants.getPostStatus(likeDetail);
            // 状态为1，则改状态为0，点赞量-1
            if (LIKE.getCode().equals(status)) {
                if (redisUtils.saveLike(postLike)) {
                    return Result.success();
                } else {
                    log.error("服务器出错，点赞修改失败");
                    return Result.error("修改失败");
                }
            }
            // 状态为0，则报错
            else return Result.error("您未点赞过");
        }
        // 没有--->在数据库中查询是否有该点赞记录
        PostLike postLike1 = this.lambdaQuery().eq(PostLike::getPostId, postLike.getPostId())
                .eq(PostLike::getUid, postLike.getUid()).one();
        // 有--->查看记录状态
        if (postLike1 != null) {
            // 状态为1，则改状态为0，并保存在redis中，点赞量-1
            if (LIKE.getCode().equals(postLike1.getStatus())) {
                postLike1.setStatus(postLike.getStatus());
                if (redisUtils.saveLike(postLike)) {
                    return Result.success();
                } else {
                    log.error("服务器出错，点赞修改失败");
                    return Result.error("修改失败");
                }
            }
            // 状态为0，则报错
            else return Result.error("您未点赞过");
        }
        // 没有--->从未点赞过，报错
        return Result.error("您未点赞过");
    }

    @Override
    @Transactional
    public void saveLikeFromRedis() {
        // 将点赞记录包装成PostLike对象集合
        ArrayList<PostLike> likes = redisUtils.getPostLikes();
        if(likes.isEmpty()){
            return;
        }
        // 增强for一一处理
        for(PostLike like : likes){
            // 与mysql中的记录进行对比，去重
            PostLike postLike = lambdaQuery().eq(PostLike::getPostId, like.getPostId())
                    .eq(PostLike::getUid, like.getUid()).one();
            // 若没有记录则增加记录
            if(Objects.isNull(postLike)){
                save(like);
            }
            // 若有记录则更新记录
            else {
                postLike.setStatus(like.getStatus());
                updateById(postLike);
            }
        }
    }

    @Override
    @Transactional
    public void saveLikeNumFromRedis() {
        // 将redis中的点赞数记录封装成集合
        ArrayList<Post> postList = redisUtils.getPostLikeList();
        if(postList.isEmpty()){
            return;
        }
        // 增强for一一处理
        for(Post postDTO : postList){
            Post post = postService.getOnePost(postDTO.getId());
            if(Objects.isNull(post)){
                return;
            }
            // 将mysql中的点赞数记录与redis中的进行相加
            Integer likeNum = post.getLikes() + postDTO.getLikes();
            post.setLikes(likeNum);
            // 更新mysql中的数据
            postService.updateById(post);
        }
    }

    @Override
    public Boolean isLike(Integer postId, Integer uid) {
        Boolean like = redisUtils.isLike(postId, uid);
        if (Objects.isNull(like)){
            PostLike postLike = lambdaQuery().eq(PostLike::getPostId, postId)
                    .eq(PostLike::getUid, uid).one();
            if(Objects.isNull(postLike)){
                return false;
            }
            return LIKE.getCode().equals(postLike.getStatus());
        }
        return like;
    }

}
