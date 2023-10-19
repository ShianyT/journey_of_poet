package com.zhengaicha.journey_of_poet.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhengaicha.journey_of_poet.entity.GameComment;
import com.zhengaicha.journey_of_poet.mapper.GameCommentsMapper;
import com.zhengaicha.journey_of_poet.service.GameCommentsService;
import org.springframework.stereotype.Service;

@Service
public class GameCommentsServiceImpl extends ServiceImpl<GameCommentsMapper, GameComment>
        implements GameCommentsService {
}
