package com.zhengaicha.journey_of_poet.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhengaicha.journey_of_poet.dto.Result;
import com.zhengaicha.journey_of_poet.entity.PostLike;

public interface PostLikeService extends IService<PostLike> {
    public Result like(PostLike postLike);

    /**
     * 增加点赞
     */
    public Result addLike(PostLike postLike);

    /**
     * 减少点赞
     */
    public Result deleteLike(PostLike postLike);

    /**
     * 将点赞数据存入mysql
     */
    public void saveLikeFromRedis();

    /**
     * 将点赞量存入mysql
     */
    public void saveLikeNumFromRedis();

    /**
     * 查询用户是否点赞
     */
    public Boolean isLike(Integer postId, Integer uid);
}
