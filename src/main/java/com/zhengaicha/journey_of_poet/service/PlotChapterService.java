package com.zhengaicha.journey_of_poet.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhengaicha.journey_of_poet.entity.PlotChapter;

public interface PlotChapterService extends IService<PlotChapter> {
    public PlotChapter getOne(Integer chapterId);
}
