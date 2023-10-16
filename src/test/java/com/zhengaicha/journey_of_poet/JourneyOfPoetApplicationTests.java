package com.zhengaicha.journey_of_poet;


import cn.hutool.core.lang.UUID;
import com.zhengaicha.journey_of_poet.service.UserService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.Objects;


@SpringBootTest(classes = JourneyOfPoetApplication.class)
class JourneyOfPoetApplicationTests {
    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    UserService userService;

    @Test
    void text() {
        String ping = Objects.requireNonNull(stringRedisTemplate.getConnectionFactory()).getConnection().ping();
        System.out.println(ping);
    }

    @Test
    void uuidTest() {
        long uid = UUID.randomUUID().getLeastSignificantBits();
        uid = uid < 0 ? -uid : uid;
        String code = UUID.randomUUID().toString(true).substring(0,6); //保持在六位数
        System.out.println(uid+"," +code);
    }

    // @Test
    // void contextLoads() {
    //     LoginDTO loginDTO = new LoginDTO();
    //     String token = JwtUtils.generateJwt("123456");
    //     Claims claims = JwtUtils.parseJwt(token);
    //     System.out.println(claims.get("uid"));
    // }

    @Test
    void sendCodeTest() {
        // userService.sendCode("shianywen@sina.com");
    }

}
