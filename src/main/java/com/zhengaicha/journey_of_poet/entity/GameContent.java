package com.zhengaicha.journey_of_poet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.models.auth.In;
import lombok.Data;

import java.util.concurrent.ExecutorService;

@Data
@TableName("tb_game_content")
public class GameContent {
    /**
     * 文本id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 未切割的文本
     */
    private String content;

    /**
     * 文本形式  值含义：0为旁白，1为对话，2为选项
     */
    private Integer form;

    /**
     * 场景，前端判断该文本在哪个场景
     */
    private Integer scene;

    /**
     * 选项支线操作和内容文本
     */
    private String branchContent;

    /**
     * 进行切割处理后的文本
     */
    @TableField(exist = false)
    private String[] contentArray;

    /**
     * 对支线文本进行切割处理，包含 选项:操作:文本（有些不一定有文本，视数组长度判断）
     */
    @TableField(exist = false)
    private String[] branchContentArray;

}
