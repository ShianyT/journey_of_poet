package com.zhengaicha.journey_of_poet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhengaicha.journey_of_poet.dto.Result;
import com.zhengaicha.journey_of_poet.entity.GameContent;
import com.zhengaicha.journey_of_poet.mapper.GameContentMapper;
import com.zhengaicha.journey_of_poet.service.GameContentService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
public class GameContentServiceImpl
        extends ServiceImpl<GameContentMapper, GameContent>
        implements GameContentService {

    public Result getContent(Integer sceneId) {
        // 获取一整个场景文本数组
        List<GameContent> contentList = this.lambdaQuery()
                .eq(GameContent::getSceneId, sceneId).orderByAsc(GameContent::getId).list();
        if (! contentList.isEmpty()) {
            // 对每个文本进行处理
            for (int i = 0; i < contentList.size(); i++) {
                GameContent content = contentList.get(i);
                content.setContentArray(content.getContent().split("\r\n"));
                if (content.getBranchContent() != null) {
                    content.setBranchContentArray(content.getBranchContent().split(":"));
                }
                contentList.set(i,content);
            }
            return Result.success(contentList);
        }
        return Result.error("剧情已到结尾");
    }
}
