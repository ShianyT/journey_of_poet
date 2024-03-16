package com.zhengaicha.journey_of_poet.filter;


import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhengaicha.journey_of_poet.dto.UserDTO;
import com.zhengaicha.journey_of_poet.utils.JwtUtil;
import com.zhengaicha.journey_of_poet.utils.RedisUtils;
import com.zhengaicha.journey_of_poet.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.zhengaicha.journey_of_poet.utils.RedisConstants.LOGIN_TOKEN_KEY;
import static com.zhengaicha.journey_of_poet.utils.RedisConstants.USER_TOKEN_TTL;
import static com.zhengaicha.journey_of_poet.utils.SystemConstants.USER_AUTHORIZATION;

@Component
public class WebSocketFilter implements Filter {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisUtils redisUtils;

    private static final String[] FILTER_LIST = {"/battles/send"};

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String servletPath = request.getServletPath();

        boolean match = Arrays.stream(FILTER_LIST).anyMatch(servletPath::startsWith);
        if (match) {
            String token = request.getHeader(USER_AUTHORIZATION);
            if (!StringUtils.hasText(token))
                token = request.getParameter(USER_AUTHORIZATION);
            if (!StrUtil.isBlank(token)) {
                // 获取UserDTO
                // 解析token获取UserDTO对象
                UserDTO userDTO = jwtUtil.parseJwt(token);
                // 若token不为空
                if (!Objects.isNull(userDTO)) {
                    // 若token在redis中有效(为用户当前的token)
                    if (redisUtils.isTokenValid(userDTO.getUid(), token)) {
                        // 令牌过期则刷新令牌
                        if (jwtUtil.isJwtExpire(token)) {
                            // 若redis中的未过期而令牌过期，则重新给新的令牌
                            if (stringRedisTemplate.opsForHash().get(LOGIN_TOKEN_KEY, userDTO.getUid().toString()) != null) {
                                String newToken = jwtUtil.createToken(userDTO);
                                redisUtils.saveToken(userDTO.getUid(), newToken);
                                response.setHeader(USER_AUTHORIZATION, newToken);
                                response.addHeader("Access-Control-Expose-Headers", USER_AUTHORIZATION);
                                // 若token有效，保存用户信息到 ThreadLocal
                                UserHolder.saveUser(userDTO);
                                filterChain.doFilter(servletRequest, servletResponse);
                            } else response.setStatus(401);
                        } else {
                            // 若token有效，保存用户信息到 ThreadLocal
                            UserHolder.saveUser(userDTO);
                            filterChain.doFilter(servletRequest, servletResponse);
                        }
                    } else response.setStatus(401);
                } else response.setStatus(401);
            } else response.setStatus(401);
        } else filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
