package com.zhengaicha.journey_of_poet.filter;


import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhengaicha.journey_of_poet.dto.UserDTO;
import com.zhengaicha.journey_of_poet.utils.UserHolder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static com.zhengaicha.journey_of_poet.utils.RedisConstants.LOGIN_TOKEN_KEY;
import static com.zhengaicha.journey_of_poet.utils.RedisConstants.USER_TOKEN_TTL;
import static com.zhengaicha.journey_of_poet.utils.SystemConstants.USER_AUTHORIZATION;

@Component
public class WebSocketFilter implements Filter {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

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
        if(match){
            String token = request.getHeader(USER_AUTHORIZATION);
            if (!StringUtils.hasText(token))
                token = request.getParameter(USER_AUTHORIZATION);
            if (!StrUtil.isBlank(token)) {
                // 获取UserDTO
                String UserJson = stringRedisTemplate.opsForValue().get(LOGIN_TOKEN_KEY + token);
                if(UserJson == null){
                    response.setStatus(401);
                    return;
                }
                ObjectMapper objectMapper = new ObjectMapper();
                UserDTO userDTO = objectMapper.readValue(UserJson, UserDTO.class);
                if(userDTO != null){
                    // 存在，保存用户信息到 ThreadLocal
                    UserHolder.saveUser(userDTO);
                    // 刷新token有效期
                    stringRedisTemplate.expire(LOGIN_TOKEN_KEY + token, USER_TOKEN_TTL, TimeUnit.DAYS);
                    filterChain.doFilter(request,servletResponse);
                }
            }
            // 未带token或token无效，不放行
            else response.setStatus(401);
        }
        else filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
