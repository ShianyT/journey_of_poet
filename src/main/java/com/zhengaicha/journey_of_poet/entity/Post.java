package com.zhengaicha.journey_of_poet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.elasticsearch.common.text.Text;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@TableName("tb_post")
public class Post {

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
     * 用户昵称，方便展示
     */
    @TableField(exist = false)
    private String nickname;

    /**
     * 用户头像，方便展示
     */
    @TableField(exist = false)
    private String icon;

    /**
     * 标题
     */
    private String title;

    /**
     * 帖子文本
     */
    private String content;

    /**
     * 图片，最多支持上传四张图片
     */
    private String images;

    /**
     * 点赞数量
     */
    private Integer likes;

    /**
     * 判断用户是否点赞过该帖子
     */
    @TableField(exist = false)
    private Boolean isLike;

    /**
     * 收藏数量
     */
    private Integer collections;

    /**
     * 判断用户是否收藏过该帖子
     */
    @TableField(exist = false)
    private Boolean isCollection;

    /**
     * 评论数量
     */
    private Integer comments;

    /**
     * 发布时间
     */
    private Timestamp createTime;

    /**
     * 高亮信息
     */
    @TableField(exist = false)
    private Text[] fragments;

}
