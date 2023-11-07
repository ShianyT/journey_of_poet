package com.zhengaicha.journey_of_poet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@TableName("tb_poetry_battle_detail")
@AllArgsConstructor
@NoArgsConstructor
public class PoetryBattleDetail {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户id
     */
    private Integer uid;

    /**
     * 用户发送的诗句
     */
    private String poem;

    /**
     * 诗句对应诗的id
     */
    private Integer poemId;

    /**
     * 诗句对应的诗名
     */
    private String poemTitle;

    /**
     * 对战记录id
     */
    private Integer battleRecordsId;

    /**
     * 发布时间
     */
    private Timestamp createTime;

    /**
     * 是否重复
     */
    @TableField(exist = false)
    private boolean isRepeat;

    public PoetryBattleDetail(Integer uid, String poem) {
        this.uid = uid;
        this.poem = poem;
    }
}
