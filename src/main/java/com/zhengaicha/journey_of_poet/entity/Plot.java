package com.zhengaicha.journey_of_poet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_plot")
public class Plot {
    /**
     * 文本id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 剧情文本
     */
    private String content;

    /**
     * 文本类型  值含义：0为旁白，1为对话，2为选项，3为背诵功能
     */
    private Integer type;

    /**
     * 下一条剧情文本id
     */
    private String nextPlotId;

    /**
     * 事件节点id
     */
    private Integer eventId;

    /**
     * 场景id，前端判断该文本在哪个场景
     */
    private Integer sceneId;

    /**
     * 场景id，前端判断该文本在哪个场景
     */
    private String paintingId;

    /**
     * 音乐id，前端判断该文本使用哪段音乐
     */
    private Integer musicId;

    /**
     * 视频id，当文本为结局时，前端判断调用哪条视频
     */
    private Integer videoId;
}
