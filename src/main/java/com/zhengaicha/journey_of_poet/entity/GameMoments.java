package com.zhengaicha.journey_of_poet.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.List;

@Data
@TableName("tb_game_moments")
public class GameMoments {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    /**
     * 发表朋友圈的诗人
     */
    private String poet;

    /**
     * 朋友圈文本
     */
    private String content;

    /**
     * 朋友圈发布时间
     */
    private Integer releaseTime;

    /**
     * 发布地点
     */
    private String location;

    /**
     * 该朋友圈提及了谁
     */
    private String mentionPoet;

    /**
     * 该朋友圈的评论
     */
    @TableField(exist = false)
    private List<GameComment> gameComments;

    /**
     * 该朋友圈的点赞
     */
    @TableField(exist = false)
    private List<GameLiked> gameLiked;

    /**
     * 距离当前场景的时间差
     */
    @TableField(exist = false)
    private String timeDifference;
}
