package com.zhengaicha.journey_of_poet.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhengaicha.journey_of_poet.dto.Result;
import com.zhengaicha.journey_of_poet.entity.GameComments;
import com.zhengaicha.journey_of_poet.entity.GameLiked;
import com.zhengaicha.journey_of_poet.entity.GameMoments;
import com.zhengaicha.journey_of_poet.entity.GameScene;
import com.zhengaicha.journey_of_poet.mapper.GameCommentsMapper;
import com.zhengaicha.journey_of_poet.mapper.GameLikedMapper;
import com.zhengaicha.journey_of_poet.mapper.GameMomentsMapper;
import com.zhengaicha.journey_of_poet.mapper.GameSceneMapper;
import com.zhengaicha.journey_of_poet.service.GameCommentsService;
import com.zhengaicha.journey_of_poet.service.GameLikedService;
import com.zhengaicha.journey_of_poet.service.GameMomentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class GameMomentsServiceImpl extends ServiceImpl<GameMomentsMapper, GameMoments>
        implements GameMomentsService {

    @Autowired
    private GameSceneMapper gameSceneMapper;

    @Autowired
    private GameLikedService gameLikedService;

    @Autowired
    private GameCommentsService gameCommentsService;

    @Override
    public Result getGameMoments(Integer scene, Integer currentPage) {
        if(scene < 1)
            return Result.error("场景参数错误");

        // 获得场景id
        GameScene gameScene = gameSceneMapper.selectById(scene);
        if (Objects.isNull(gameScene))
            return Result.error("场景不存在");

        // 查询在当前场景时间之前的朋友圈
        Page<GameMoments> page = lambdaQuery().lt(GameMoments::getReleaseTime, gameScene.getTime())
                .page(new Page<>(currentPage, 5));
        List<GameMoments> gameMomentsList = page.getRecords();

        if (gameMomentsList.isEmpty())
            return Result.error("朋友圈记录已到结尾");

        // 获取朋友圈评论
        for (int i = 0; i < gameMomentsList.size(); i++) {
            GameMoments gameMoments = gameMomentsList.get(i);
            List<GameComments> gameCommentsList = gameCommentsService.lambdaQuery()
                    .eq(GameComments::getMomentsId, gameMoments.getId()).list();
            if (!gameCommentsList.isEmpty())
                gameMoments.setGameComments(gameCommentsList);

            // 获取朋友圈点赞
            List<GameLiked> gameLikedList = gameLikedService.lambdaQuery()
                    .eq(GameLiked::getMomentsId, gameMoments.getId()).list();
            if (!gameLikedList.isEmpty())
                gameMoments.setGameLiked(gameLikedList);


            // 获取距离当前场景时间的时间差
            int timeDifference = gameScene.getTime() - gameMoments.getReleaseTime();
            if (timeDifference > 0)
                gameMoments.setTimeDifference(timeDifference + "年前");

            gameMomentsList.set(i, gameMoments);
        }
        return Result.success(gameMomentsList);
    }
}