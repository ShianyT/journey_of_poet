package com.zhengaicha.journey_of_poet.utils;

import java.sql.Timestamp;

/**
 * redis相关常数配置
 */
public class RedisConstants {
    /**
     * 验证码key前缀
     */
    public static final String LOGIN_CODE_KEY = "login:code:";

    /**
     * tokenKey前缀
     */
    public static final String LOGIN_TOKEN_KEY = "login:token:";

    /**
     * 帖子图片前缀
     */
    public static final String POST_IMAGES_KEY_PREFIX = "post:images:";

    /**
     * 验证码过期时间
     */
    public static final Long LOGIN_CODE_TTL = 3L;

    /**
     * token过期时间
     */
    public static final Long USER_TOKEN_TTL = 10L;

    /**
     * 帖子点赞详情前缀
     */
    public static final String POST_LIKE_DETAIL_KEY = "post:like:detail";

    /**
     * 帖子点赞数量前缀
     */
    public static final String POST_LIKE_NUM_KEY = "post:like:num";

    /**
     * 在redis中存储like的HashKey
     */
    public static String getLikeKey(Integer postId,Integer Uid){
        return postId + "::" + Uid;
    }

    /**
     * 在redis中存储like详情的HashValue
     */
    public static String getLikeDetailValue(Integer status, Timestamp createTime){
        return status + "::" + createTime;
    }

    /**
     * 获取likeDetailValue中的状态
     */
    public static Integer getLikeStatus(String likeDetail){
        String statusStr = likeDetail.split("::")[0];
        return Integer.parseInt(statusStr);
    }

    /**
     * 获取likeDetailValue中的点赞时间
     */
    public static Timestamp getLikeCreateTime(String likeDetail){
        String CreateTimeStr = likeDetail.split("::")[1];
        return Timestamp.valueOf(CreateTimeStr);
    }

    public static String getLikeNumKey(int postId){
        return String.valueOf(postId);
    }

}
