package com.zhengaicha.journey_of_poet.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhengaicha.journey_of_poet.entity.PoetryBattleDetail;
import com.zhengaicha.journey_of_poet.entity.PoetryBattleRecords;

import java.util.List;

public interface PoetryBattleDetailService extends IService<PoetryBattleDetail> {
    public PoetryBattleDetail matchPoem(Integer userUid, String poetry);

    public void saveBattleDetail(PoetryBattleRecords poetryBattleRecords);

    public List<PoetryBattleDetail> getBattleDetail(Integer poetryBattleRecordId);
}
