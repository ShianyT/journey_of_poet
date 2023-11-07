package com.zhengaicha.journey_of_poet.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhengaicha.journey_of_poet.dto.UserDTO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.zhengaicha.journey_of_poet.utils.RedisConstants.LOGIN_CODE_KEY;
import static com.zhengaicha.journey_of_poet.utils.RedisConstants.LOGIN_TOKEN_KEY;
import static com.zhengaicha.journey_of_poet.utils.RedisConstants.USER_TOKEN_TTL;
import static com.zhengaicha.journey_of_poet.utils.SystemConstants.USER_AUTHORIZATION;

public class RefreshTokenInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate stringRedisTemplate;

    public RefreshTokenInterceptor(StringRedisTemplate stringRedisTemplate){
        this.stringRedisTemplate = stringRedisTemplate;
    }


    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception  {
        // 放掉第一次options请求
        if("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        // 放开.html .css .js 等请求路径
        if(request.getServletPath().contains(".")){
            return true;
        }
        // 获取请求头中的token
        String token = request.getHeader(USER_AUTHORIZATION);
        if (!StringUtils.hasText(token))
            token = request.getParameter(USER_AUTHORIZATION);
        if (StrUtil.isBlank(token)) {
            return true;
        }

        // 获取UserDTO
        String UserJson = stringRedisTemplate.opsForValue().get(LOGIN_TOKEN_KEY + token);
        if(StrUtil.isBlank(UserJson)){
            return true;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        UserDTO userDTO = objectMapper.readValue(UserJson, UserDTO.class);
        if(Objects.isNull(userDTO)){
            return true;
        }
        // 存在，保存用户信息到 ThreadLocal
        UserHolder.saveUser(userDTO);
        // 刷新token有效期
        stringRedisTemplate.expire(LOGIN_TOKEN_KEY + token, USER_TOKEN_TTL, TimeUnit.DAYS);
        // 放行
        return true;
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // 移除用户
        UserHolder.removeUser();
    }
}
