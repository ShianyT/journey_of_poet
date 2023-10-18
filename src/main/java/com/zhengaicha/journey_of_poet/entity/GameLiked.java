package com.zhengaicha.journey_of_poet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_game_liked")
public class GameLiked {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 点赞对应的朋友圈id
     */
    @TableField(value = "mid")
    private Integer momentsId;

    /**
     * 点赞的人物名字
     */
    private String name;
}
