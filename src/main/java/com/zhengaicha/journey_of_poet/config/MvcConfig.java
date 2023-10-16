package com.zhengaicha.journey_of_poet.config;
// TODO 为测试方便暂时注释

import com.zhengaicha.journey_of_poet.utils.LoginInterceptor;
import com.zhengaicha.journey_of_poet.utils.RefreshTokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .excludePathPatterns(
                        "/users/code",
                        "/users/create",
                        "/users/login",
                        "/users/modify",
                        "/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**",
                        "/games/content/**",
                        "/imgs/**"
                ).order(1);
        // token刷新的拦截器
        registry.addInterceptor(new RefreshTokenInterceptor(stringRedisTemplate))
                .excludePathPatterns(
                        "/users/code",
                        "/users/create",
                        "/users/login",
                        "/users/modify",
                        "/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**",
                        "/games/content/**",
                        "/imgs/**"
                ).order(0);
    }
}
