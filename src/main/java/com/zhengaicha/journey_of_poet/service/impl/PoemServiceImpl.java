package com.zhengaicha.journey_of_poet.service.impl;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhengaicha.journey_of_poet.dto.Result;
import com.zhengaicha.journey_of_poet.entity.Poem;
import com.zhengaicha.journey_of_poet.mapper.PoemMapper;
import com.zhengaicha.journey_of_poet.service.PoemService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class PoemServiceImpl extends ServiceImpl<PoemMapper, Poem> implements PoemService {
    @Override
    public Result poem(Integer poemId) {
        Poem one = lambdaQuery().eq(Poem::getId, poemId).one();
        JSONObject entries = new JSONObject(one.getOther());
        String s = entries.toString();
        one.setJsonOther(entries);
        return Result.success(one);
    }

    @Override
    public Result search(String keywords, int currentPage) {
        if(currentPage < 1){
            return Result.error("页码错误");
        }
        List<Poem> records = lambdaQuery().like(Poem::getContent, keywords)
                .page(new Page<>(currentPage, 20)).getRecords();
        if(records.isEmpty()){
            return Result.error("已无更多记录");
        }
        for(Poem poem : records){
            JSONObject entries = new JSONObject(poem.getOther());
            String s = entries.toString();
            poem.setJsonOther(entries);
        }
        return Result.success(records);
    }

    @Override
    public Poem getPoemByPoetry(String poetry) {
        if(poetry.isEmpty()){
            return null;
        }
        return lambdaQuery().like(Poem::getContent, poetry).last("limit 1").one();
    }
}
