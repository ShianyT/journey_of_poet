package com.zhengaicha.journey_of_poet.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhengaicha.journey_of_poet.dto.Result;
import com.zhengaicha.journey_of_poet.entity.Poem;

public interface PoemService extends IService<Poem> {
    public Result poem(Integer poemId);

    public Result search(String keywords, int currentPage);

    public Poem getPoemByPoetry(String poetry);
}
