package com.zhengaicha.journey_of_poet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户详细信息
 */
@Data
@TableName("tb_user_info")
public class UserInfo {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private Integer uid;
    private String mail;
    private String nickname;
    private String avatar;
    private Integer gender;
    private String signature;
    private Integer followers;
    private Integer fans;
}
