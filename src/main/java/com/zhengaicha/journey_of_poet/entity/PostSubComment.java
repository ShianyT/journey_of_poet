package com.zhengaicha.journey_of_poet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("tb_post_sub_comments")
public class PostSubComment {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 主表评论的id
     */
    private Integer commentId;

    /**
     * 用户id
     */
    private Integer uid;

    /**
     * 被评论的uid
     */
    private Integer commentedUid;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 点赞数
     */
    private Integer likes;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
