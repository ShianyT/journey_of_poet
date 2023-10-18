package com.zhengaicha.journey_of_poet.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhengaicha.journey_of_poet.entity.GameLiked;
import com.zhengaicha.journey_of_poet.mapper.GameLikedMapper;
import com.zhengaicha.journey_of_poet.service.GameLikedService;
import org.springframework.stereotype.Service;

@Service
public class GameLikedServiceImpl extends ServiceImpl<GameLikedMapper, GameLiked> implements GameLikedService {
}
