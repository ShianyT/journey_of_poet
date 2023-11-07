package com.zhengaicha.journey_of_poet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@TableName("tb_poetry_battle_records")
@AllArgsConstructor
@NoArgsConstructor
public class PoetryBattleRecords {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 先手用户id
     */
    private Integer beforeUid;

    /**
     * 先手用户昵称，方便展示
     */
    @TableField(exist = false)
    private String beforeNickname;

    /**
     * 先手用户头像，方便展示
     */
    @TableField(exist = false)
    private String beforeIcon;

    /**
     * 后手用户id
     */
    private Integer afterUid;

    /**
     * 后手用户昵称，方便展示
     */
    @TableField(exist = false)
    private String afterNickname;

    /**
     * 后手用户头像，方便展示
     */
    @TableField(exist = false)
    private String afterIcon;

    /**
     * 关键字
     */
    private String keyword;

    /**
     * 对战结果
     */
    private Integer outcome;

    /**
     * 创建时间
     */
    private Timestamp createTime;

    /**
     * 对战记录详情，方便展示
     */
    @TableField(exist = false)
    private List<PoetryBattleDetail> poetryBattleDetails;

    /**
     * 判断用户是否已经从redis中使用过该记录
     */
    @TableField(exist = false)
    private boolean isUse;

    public PoetryBattleRecords(Integer uid1, Integer uid2, String keyword, Timestamp timestamp) {
        this.beforeUid = uid1;
        this.afterUid = uid2;
        this.keyword = keyword;
        this.createTime = timestamp;
    }
}
