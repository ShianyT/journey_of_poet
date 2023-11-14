package com.zhengaicha.journey_of_poet.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhengaicha.journey_of_poet.dto.Result;
import com.zhengaicha.journey_of_poet.entity.Plot;

public interface PlotService extends IService<Plot>{
    public Result getPlotContent(Integer eventId, String nextPlotId);

    public Result getPlotProgress();

    public Result savePlotProgress(Integer eventId);

    public Result unlockChapter(Integer chapterId);
}
