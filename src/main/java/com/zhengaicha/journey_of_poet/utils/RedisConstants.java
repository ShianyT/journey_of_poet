package com.zhengaicha.journey_of_poet.utils;

import java.sql.Timestamp;

/**
 * redis相关常数配置
 */
public class RedisConstants {
    // --------------------------------------登录验证相关---------------------------------------- //
    /**
     * 验证码key前缀
     */
    public static final String LOGIN_CODE_KEY_PREFIX = "login:code:";
    /**
     * 验证码过期时间
     */
    public static final Long LOGIN_CODE_TTL = 3L;


    /**
     * tokenKey前缀
     */
    public static final String LOGIN_TOKEN_KEY_PREFIX = "login:token:";
    /**
     * token过期时间
     */
    public static final Long USER_TOKEN_TTL = 10L;

    // --------------------------------------社区帖子相关---------------------------------------- //

    /**
     * 帖子图片前缀
     */
    public static final String POST_IMAGES_KEY_PREFIX = "post:images:";

    /**
     * 帖子点赞详情前缀
     */
    public static final String POST_LIKE_DETAIL_KEY_PREFIX = "post:like:detail";

    /**
     * 帖子点赞数量前缀
     */
    public static final String POST_LIKE_NUM_KEY_PREFIX = "post:like:num";

    /**
     * 帖子收藏详情前缀
     */
    public static final String POST_COLLECTION_DETAIL_KEY_PREFIX = "post:collection:detail";

    /**
     * 帖子收藏数量前缀
     */
    public static final String POST_COLLECTION_NUM_KEY_PREFIX = "post:collection:num";

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

    // --------------------------------------飞花令相关---------------------------------------- //

    /**
     * 对战记录key前缀
     */
    public static final String BATTLE_RECORD_KEY_PREFIX = "battle:record:";
    /**
     * 获取完整对战记录的key
     */
    public static String getBattleRecordKey(Integer uid1,Integer uid2){
        return BATTLE_RECORD_KEY_PREFIX + uid1 + "::" + uid2;
    }


    /**
     * 对战详情记录key前缀
     */
    public static final String BATTLE_DETAIL_KEY_PREFIX = "battle:detail:";
    /**
     * 获取完整对战详情的key
     */
    public static String getBattleDetailKey(Integer poetryBattleRecordId){
        return BATTLE_DETAIL_KEY_PREFIX + poetryBattleRecordId;
    }


    /**
     * 记录正在对战的用户
     */
    public static final String BATTLE_USER_KEY_PREFIX = "battle:user:";
    /**
     * 获取正在对战用户的key
     */
    public static String getBattleUserKey(Integer uid){
        return BATTLE_USER_KEY_PREFIX + uid;
    }

}
