package com.zhengaicha.journey_of_poet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 用户昵称，方便展示
     */
    @TableField(exist = false)
    private String nickname;

    /**
     * 用户头像，方便展示
     */
    @TableField(exist = false)
    private String icon;

    /**
     * 被评论用户昵称，方便展示
     */
    @TableField(exist = false)
    private String commentedNickname;

}
