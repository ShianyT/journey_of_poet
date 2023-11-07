package com.zhengaicha.journey_of_poet;

import com.zhengaicha.journey_of_poet.service.PoetryBattleRecordsService;
import com.zhengaicha.journey_of_poet.service.impl.PoetryBattleRecordsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@SpringBootApplication
public class JourneyOfPoetApplication {
    public static void main(String[] args) {
        SpringApplication.run(JourneyOfPoetApplication.class, args);
    }
}
