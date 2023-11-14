package com.zhengaicha.journey_of_poet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_plot_chapter")
public class PlotChapter {

    /**
     * 文本id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 章节名称
     */
    private String title;

    /**
     * 诗人名字
     */
    private String poet;

}
