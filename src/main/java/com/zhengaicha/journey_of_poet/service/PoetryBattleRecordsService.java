package com.zhengaicha.journey_of_poet.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhengaicha.journey_of_poet.dto.Result;
import com.zhengaicha.journey_of_poet.entity.PoetryBattleRecords;

import javax.websocket.Session;

public interface PoetryBattleRecordsService extends IService<PoetryBattleRecords> {
    public Result add();

    public void match();

    public Result cancelMatch();

    public Result history(int currentPage);

    public Result learn(String keyword);

    public Result outcome(PoetryBattleRecords poetryBattleRecords);

    public Result getKeywords();
}
