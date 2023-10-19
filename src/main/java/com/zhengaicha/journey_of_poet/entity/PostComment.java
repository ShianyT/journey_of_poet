package com.zhengaicha.journey_of_poet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("tb_post_comments")
public class PostComment {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 帖子id
     */
    private Integer postId;

    /**
     * 用户id
     */
    private Integer uid;

    /**
     * 点赞数
     */
    private Integer likes;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 子评论
     */
    @TableField(exist = false)
    private PostSubComment postSubComment;

    /**
     * 子评论数量
     */
    @TableField(exist = false)
    private long postSubCommentNum;

}
