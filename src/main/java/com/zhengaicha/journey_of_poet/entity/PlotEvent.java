package com.zhengaicha.journey_of_poet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_plot_event")
public class PlotEvent {
    /**
     * 文本id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 事件名称
     */
    private String title;

    /**
     * 该事件所处章节id
     */
    private Integer chapterId;

    /**
     * 章节名称
     */
    @TableField(exist = false)
    private String chapterTitle;
}
