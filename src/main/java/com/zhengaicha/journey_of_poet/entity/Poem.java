package com.zhengaicha.journey_of_poet.entity;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


@Data
@TableName("tb_poem")
public class Poem {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 朝代
     */
    private String dynasty;

    /**
     * 诗人
     */
    private String poet;

    /**
     * 诗名
     */
    private String name;

    /**
     * 诗词内容
     */
    private String content;

    /**
     * 诗词的其他内容，如注释，赏析等
     */
    private String other;

    /**
     * json格式的other属性
     */
    @TableField(exist = false)
    private JSONObject jsonOther;

}
