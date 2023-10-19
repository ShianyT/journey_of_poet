package com.zhengaicha.journey_of_poet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "tb_game_comments")
public class GameComment {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    /**
     * 评论对应的朋友圈id
     */
    @TableField(value = "mid")
    private Integer momentsId;

    /**
     * 评论内容
     */
    private String comment;

    /**
     * 评论的诗人
     */
    private String poet;


    /**
     * 回复的诗人名字
     */
    private String replyPoet;
}
