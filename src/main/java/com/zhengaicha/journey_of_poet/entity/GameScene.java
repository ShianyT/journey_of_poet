package com.zhengaicha.journey_of_poet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_game_sence")
public class GameScene {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 场景时间
     */
    private Integer time;
}
