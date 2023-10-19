package com.zhengaicha.journey_of_poet.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhengaicha.journey_of_poet.dto.Result;
import com.zhengaicha.journey_of_poet.entity.GameContent;

public interface GameContentService extends IService<GameContent> {

    public Result getContent(Integer sceneId);
}
