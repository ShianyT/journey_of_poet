package com.zhengaicha.journey_of_poet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户详细信息
 */
@Data
@TableName("tb_user_info")
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
     * 性别，-1为未知，0为女，1为男，默认为-1
     */
    private Integer gender;

    /**
     * 个性签名
     */
    private String signature;

    /**
     * 关注数
     */
    private Integer followers;

    /**
     * 粉丝数
     */
    private Integer fans;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

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
