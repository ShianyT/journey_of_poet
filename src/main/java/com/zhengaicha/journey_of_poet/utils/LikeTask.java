package com.zhengaicha.journey_of_poet.utils;


import com.zhengaicha.journey_of_poet.service.PostLikeService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

@Slf4j
public class LikeTask extends QuartzJobBean {

    @Autowired
    private PostLikeService postLikeService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.info("--------likeTaskExecute--------");
        postLikeService.saveLikeFromRedis();
        postLikeService.saveLikeNumFromRedis();

    }
}
