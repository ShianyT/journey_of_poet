package com.zhengaicha.journey_of_poet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("tb_post_likes")
public class PostLike {
    /**
     * 点赞id，主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 帖子id
     */
    private Integer postId;

    /**
     * 用户uid
     */
    private Integer uid;

    /**
     * 是否取消点赞 0为取消点赞 1为点赞
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Timestamp createTime;
}
