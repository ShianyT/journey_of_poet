package com.zhengaicha.journey_of_poet.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhengaicha.journey_of_poet.entity.PlotEvent;

public interface PlotEventService extends IService<PlotEvent> {
    public PlotEvent getOne(Integer plotProgression);
}
