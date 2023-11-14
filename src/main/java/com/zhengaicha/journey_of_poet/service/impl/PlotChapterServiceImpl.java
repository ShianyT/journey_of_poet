package com.zhengaicha.journey_of_poet.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhengaicha.journey_of_poet.entity.PlotChapter;
import com.zhengaicha.journey_of_poet.mapper.PlotChapterMapper;
import com.zhengaicha.journey_of_poet.service.PlotChapterService;
import org.springframework.stereotype.Service;

@Service
public class PlotChapterServiceImpl extends ServiceImpl<PlotChapterMapper, PlotChapter>
        implements PlotChapterService {
    @Override
    public PlotChapter getOne(Integer chapterId) {

        return lambdaQuery().eq(PlotChapter::getId,chapterId).one();
    }
}
