package com.zhengaicha.journey_of_poet.utils;

import io.swagger.models.auth.In;

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
     * 帖子点赞详情前缀
     */
    public static final String POST_COLLECTION_DETAIL_KEY = "post:collection:detail";

    /**
     * 帖子点赞数量前缀
     */
    public static final String POST_COLLECTION_NUM_KEY = "post:collection:num";

    /**
     * 对战详情记录key前缀
     */
    public static final String BATTLE_DETAIL_KEY = "battle:detail:";

    /**
     * 对战记录key前缀
     */
    public static final String BATTLE_RECORD_KEY = "battle:record:";

    /**
     * 获取完整对战详情的key
     */
    public static String getBattleDetailKey(Integer poetryBattleRecordId){
        return BATTLE_DETAIL_KEY + poetryBattleRecordId;
    }

    /**
     * 获取完整对战记录的key
     */
    public static String getBattleRecordKey(Integer uid1,Integer uid2){
        return BATTLE_RECORD_KEY + uid1 + "::" + uid2;
    }

    /**
     * 在redis中存储postLike或者postCollection的HashKey
     */
    public static String getPostKey(Integer postId, Integer Uid){
        return postId + "::" + Uid;
    }

    /**
     * 获得将在redis中PostKey的HashValue
     */
    public static String getPostDetailValue(Integer status, Timestamp createTime){
        return status + "::" + createTime;
    }

    /**
     * 获取PostKey的HashValue中的状态
     */
    public static Integer getPostStatus(String likeDetail){
        String statusStr = likeDetail.split("::")[0];
        return Integer.parseInt(statusStr);
    }

    /**
     * 获取PostKey的HashValue中的创建时间
     */
    public static Timestamp getPostCreateTime(String likeDetail){
        String CreateTimeStr = likeDetail.split("::")[1];
        return Timestamp.valueOf(CreateTimeStr);
    }

    /**
     * 获取postLike或者postCollection数量的HashKey
     */
    public static String getPostNumKey(int postId){
        return String.valueOf(postId);
    }

}
