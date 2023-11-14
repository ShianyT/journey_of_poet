package com.zhengaicha.journey_of_poet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * 用户关于剧情，游戏，排名等详细信息
 */
@Data
@TableName("tb_user_info")
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    /**
     * 主键，自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户个人uid
     */
    private Integer uid;

    /**
     * 用户账号拥有的金钱数（交子数）
     */
    private Integer money;

    /**
     * 用户是否处于对局状态(1为对战中)
     */
    private Integer battleStatue;

    /**
     * 剧情进度，只保存剧情事件id
     */
    private Integer plotProgression;

    /**
     * 已解锁的剧情章节id进度
     */
    private Integer unlockChapterId;

    /**
     * 创建时间
     */
    private Timestamp createTime;

    public UserInfo(Integer uid) {
        this.uid = uid;
    }

    /**
     * 昵称，用于展示
     */
    @TableField(exist = false)
    private String nickname;

    /**
     * 头像，用于展示
     */
    @TableField(exist = false)
    private String icon;
}
