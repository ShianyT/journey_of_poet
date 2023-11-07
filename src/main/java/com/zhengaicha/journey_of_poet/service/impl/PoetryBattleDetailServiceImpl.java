package com.zhengaicha.journey_of_poet.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhengaicha.journey_of_poet.dto.UserDTO;
import com.zhengaicha.journey_of_poet.entity.Poem;
import com.zhengaicha.journey_of_poet.entity.PoetryBattleDetail;
import com.zhengaicha.journey_of_poet.entity.PoetryBattleRecords;
import com.zhengaicha.journey_of_poet.mapper.PoetryBattleDetailMapper;
import com.zhengaicha.journey_of_poet.service.PoemService;
import com.zhengaicha.journey_of_poet.service.PoetryBattleDetailService;
import com.zhengaicha.journey_of_poet.utils.RedisUtils;
import com.zhengaicha.journey_of_poet.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Service
public class PoetryBattleDetailServiceImpl
        extends ServiceImpl<PoetryBattleDetailMapper, PoetryBattleDetail>
        implements PoetryBattleDetailService {

    @Autowired
    private PoemService poemService;
    @Autowired
    private RedisUtils redisUtils;

    /**
     * 匹配用户输入的诗句对应的诗词
     */
    @Override
    public PoetryBattleDetail matchPoem(Integer userUid, String poetry) {
        Poem poem = poemService.getPoemByPoetry(poetry);
        PoetryBattleDetail poetryBattleDetail = new PoetryBattleDetail(userUid,poetry);
        if(Objects.isNull(poem)){
            poetryBattleDetail.setPoemId(null);
            poetryBattleDetail.setPoemTitle(null);
        }
        else {
            poetryBattleDetail.setPoemId(poem.getId());
            poetryBattleDetail.setPoemTitle(poem.getName());
        }
        poetryBattleDetail.setCreateTime(new Timestamp(System.currentTimeMillis()));
        boolean b = redisUtils.savePoetryBattleDetail(userUid, poetryBattleDetail);
        if(!b){
            poetryBattleDetail.setRepeat(true);
        }
        return poetryBattleDetail;
    }

    @Override
    @Transactional
    public void saveBattleDetail(PoetryBattleRecords poetryBattleRecords) {
        List<PoetryBattleDetail> poetryBattleDetails =  redisUtils.getPoetryBattleDetail(poetryBattleRecords.getBeforeUid());
        if(!Objects.isNull(poetryBattleDetails)){
            for(PoetryBattleDetail poetryBattleDetail : poetryBattleDetails){
                poetryBattleDetail.setBattleRecordsId(poetryBattleRecords.getId());
                save(poetryBattleDetail);
            }
        }
        poetryBattleDetails =  redisUtils.getPoetryBattleDetail(poetryBattleRecords.getAfterUid());
        if(Objects.isNull(poetryBattleDetails)){
            return;
        }
        for(PoetryBattleDetail poetryBattleDetail : poetryBattleDetails){
            poetryBattleDetail.setBattleRecordsId(poetryBattleRecords.getId());
            save(poetryBattleDetail);
        }
    }

    @Override
    public List<PoetryBattleDetail> getBattleDetail(Integer poetryBattleRecordId) {
        return lambdaQuery().eq(PoetryBattleDetail::getBattleRecordsId, poetryBattleRecordId)
                .orderByAsc(PoetryBattleDetail::getCreateTime).list();
    }
}
