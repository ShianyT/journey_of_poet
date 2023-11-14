package com.zhengaicha.journey_of_poet.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhengaicha.journey_of_poet.dto.Result;
import com.zhengaicha.journey_of_poet.dto.UserDTO;
import com.zhengaicha.journey_of_poet.entity.Plot;
import com.zhengaicha.journey_of_poet.entity.PlotChapter;
import com.zhengaicha.journey_of_poet.entity.PlotEvent;
import com.zhengaicha.journey_of_poet.entity.UserInfo;
import com.zhengaicha.journey_of_poet.mapper.PlotMapper;
import com.zhengaicha.journey_of_poet.service.PlotChapterService;
import com.zhengaicha.journey_of_poet.service.PlotEventService;
import com.zhengaicha.journey_of_poet.service.PlotService;
import com.zhengaicha.journey_of_poet.service.UserInfoService;
import com.zhengaicha.journey_of_poet.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class PlotServiceImpl extends ServiceImpl<PlotMapper, Plot> implements PlotService {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private PlotEventService plotEventService;

    @Autowired
    private PlotChapterService plotChapterService;


    @Override
    public Result getPlotContent(Integer eventId, String nextPlotId) {
        if(eventId == null || nextPlotId == null){
            return Result.error("参数不足");
        }
        ArrayList<Plot> plots = new ArrayList<>();
        String[] split = nextPlotId.split(":");
        if(split.length < 2){
            Plot one;
            if(nextPlotId.equals("0")){
                one = lambdaQuery().eq(Plot::getEventId, eventId)
                        .orderByAsc(Plot::getId).last("limit 1").one();
            } else {
                one = lambdaQuery().eq(Plot::getId, nextPlotId).one();
            }
            if(Objects.isNull(one)){
                return Result.error("无相关剧情");
            }
            plots.add(one);
        } else {
            for(String plotId : split){
                Plot one = lambdaQuery().eq(Plot::getId, plotId).one();
                if(Objects.isNull(one)){
                    return Result.error("无相关剧情");
                }
                plots.add(one);
            }
        }
        return Result.success(plots);
    }

    @Override
    public Result getPlotProgress() {
        UserDTO user = UserHolder.getUser();
        UserInfo userInfo = userInfoService.getOne(user.getUid());
        PlotEvent plotEvent = null;
        if (userInfo.getPlotProgression() != 0) {
            plotEvent = plotEventService.getOne(userInfo.getPlotProgression());
            PlotChapter plotChapter = plotChapterService.getOne(plotEvent.getChapterId());
            plotEvent.setChapterTitle(plotChapter.getTitle());
        }
        return Result.success(plotEvent);
    }

    @Override
    public Result savePlotProgress(Integer eventId) {
        UserDTO user = UserHolder.getUser();
        UserInfo userInfo = userInfoService.getOne(user.getUid());
        // 判断当前事件是否为用户已经解锁过的
        if (userInfo.getPlotProgression() >= eventId) {
            return Result.error("");
        }
        // 判断该事件是否存在且有效
        PlotEvent plotEvent = plotEventService.getOne(eventId);
        if (plotEvent != null) {
            // 判断用户是否解锁该剧情
            if (userInfo.getUnlockChapterId() >= plotEvent.getChapterId()) {
                userInfo.setPlotProgression(eventId);
                userInfoService.updateById(userInfo);
                return Result.success();
            } else return Result.error("您未解锁该章节，无法保存进度");
        } else {
            return Result.error("该章节事件不存在");
        }
    }

    @Transactional
    @Override
    public Result unlockChapter(Integer chapterId) {
        UserDTO user = UserHolder.getUser();
        UserInfo userInfo = userInfoService.getOne(user.getUid());
        // 判断当前事件是否为用户已经解锁过的
        if (userInfo.getUnlockChapterId() >= chapterId) {
            return Result.error("");
        }
        // 判断该事件是否存在且有效
        PlotChapter plotChapter = plotChapterService.getOne(chapterId);
        if (plotChapter != null) {
            if(userInfo.getMoney() > 100){
                userInfo.setMoney(userInfo.getMoney() - 100);
                userInfo.setUnlockChapterId(chapterId);
                userInfoService.updateById(userInfo);
                return Result.success();
            } else return Result.error("交子数量不足");
        } else {
            return Result.error("该章节不存在");
        }
    }
}
