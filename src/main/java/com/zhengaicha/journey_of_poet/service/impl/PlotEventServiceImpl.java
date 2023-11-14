package com.zhengaicha.journey_of_poet.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhengaicha.journey_of_poet.entity.PlotEvent;
import com.zhengaicha.journey_of_poet.mapper.PlotEventMapper;
import com.zhengaicha.journey_of_poet.service.PlotEventService;
import org.springframework.stereotype.Service;

@Service
public class PlotEventServiceImpl extends ServiceImpl<PlotEventMapper, PlotEvent> implements PlotEventService {
    @Override
    public PlotEvent getOne(Integer id) {
        return lambdaQuery().eq(PlotEvent::getId,id).one();
    }
}
