package com.zhengaicha.journey_of_poet.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhengaicha.journey_of_poet.dto.Result;
import com.zhengaicha.journey_of_poet.entity.GameMoments;

public interface GameMomentsService extends IService<GameMoments> {
    public Result getGameMoments(Integer scene,Integer currentPage);
}
