package com.zhengaicha.journey_of_poet.utils;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhengaicha.journey_of_poet.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.zhengaicha.journey_of_poet.utils.RedisConstants.LOGIN_TOKEN_KEY;
import static com.zhengaicha.journey_of_poet.utils.RedisConstants.USER_TOKEN_TTL;
import static com.zhengaicha.journey_of_poet.utils.SystemConstants.USER_AUTHORIZATION;

public class RefreshTokenInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate stringRedisTemplate;

    private final JwtUtil jwtUtil;

    private final RedisUtils redisUtils;

    public RefreshTokenInterceptor(StringRedisTemplate stringRedisTemplate, JwtUtil jwtUtil, RedisUtils redisUtils) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.jwtUtil = jwtUtil;
        this.redisUtils = redisUtils;
    }


    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放掉第一次options请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        // 放开.html .css .js 等请求路径
        if (request.getServletPath().contains(".")) {
            return true;
        }
        // 获取请求头中的token
        String token = request.getHeader(USER_AUTHORIZATION);
        if (!StringUtils.hasText(token))
            token = request.getParameter(USER_AUTHORIZATION);
        if (StrUtil.isBlank(token)) {
            return true;
        }

        // 解析token获取UserDTO对象
        UserDTO userDTO = jwtUtil.parseJwt(token);
        // 若token无效则直接放行
        if (Objects.isNull(userDTO)) {
            return true;
        }

        // 判断token是否在redis中有效(是否为用户当前的token)
        // 若无效则直接放行
        if(!redisUtils.isTokenValid(userDTO.getUid(),token)){
            return true;
        }

        // 刷新过期令牌
        if (jwtUtil.isJwtExpire(token)) {
            // 若redis中的未过期而令牌过期，则重新给新的令牌
            if (stringRedisTemplate.opsForHash().get(LOGIN_TOKEN_KEY, userDTO.getUid().toString()) != null) {
                String newToken = jwtUtil.createToken(userDTO);
                redisUtils.saveToken(userDTO.getUid(), newToken);
                response.setHeader(USER_AUTHORIZATION, newToken);
                response.addHeader("Access-Control-Expose-Headers",USER_AUTHORIZATION);
            } else return true;
        }

        // 若token有效，保存用户信息到 ThreadLocal
        UserHolder.saveUser(userDTO);
        // 放行
        return true;
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // 移除用户
        UserHolder.removeUser();
    }
}
