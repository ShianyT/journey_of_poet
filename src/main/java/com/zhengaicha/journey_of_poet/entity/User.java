package com.zhengaicha.journey_of_poet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * 用户登陆注册实体
 */
@Data
@TableName("tb_user")
public class User {
    /**
     * 主键id，自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户个人id
     */
    private Integer uid;

    /**
     * 邮箱
     */
    private String mail;

    /**
     * 密码，加密保存
     */
    private String password;

    /**
     * 昵称，默认为随机生成
     */
    private String nickname;

    /**
     * 头像，存储的是路径
     */
    private String icon;

    /**
     * 性别，-1为未知，0为女，1为男，默认为-1
     */
    private Integer gender;

    /**
     * 个性签名
     */
    private String signature;

    /**
     * 创建时间
     */
    private Timestamp createTime;

    public User(Integer uid, String mail, String password, String nickname, String icon) {
        this.uid = uid;
        this.mail = mail;
        this.password = password;
        this.nickname = nickname;
        this.icon = icon;
    }
}
