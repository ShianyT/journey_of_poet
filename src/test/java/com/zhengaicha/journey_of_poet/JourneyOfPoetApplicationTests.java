package com.zhengaicha.journey_of_poet;


import com.zhengaicha.journey_of_poet.service.UserService;
import com.zhengaicha.journey_of_poet.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

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
    void contextLoads() {
        // Map<String,Object> map = new HashMap<>();
        // map.put("id", 1);
        // String jwt = JwtUtils.generateJwt(map);
        // System.out.println(jwt);
        Claims claims = JwtUtils.parseJwt("eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwiZXhwIjoxNzAyNzU0MTc0fQ.TBFZy6TOFqPlKkI43l1wLPXR90kdDCScneiayDx6O84");
        Integer id = (Integer) claims.get("id");
        System.out.println(id);

    }

    @Test
    void sendCodeTest() {
        userService.sendCode("shianywen@sina.com");
    }

}
