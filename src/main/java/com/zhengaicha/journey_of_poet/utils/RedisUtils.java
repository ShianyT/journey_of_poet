package com.zhengaicha.journey_of_poet.utils;

import cn.hutool.core.util.StrUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import static com.zhengaicha.journey_of_poet.utils.RedisConstants.LOGIN_CODE_KEY;

@Component
public class RedisUtils {

    private StringRedisTemplate stringRedisTemplate;

    public RedisUtils(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 存入单个hash类型缓存
     */
    public void hPut(String key, String filed, String value) {
        stringRedisTemplate.opsForHash().put(key, filed, value);
    }

    /**
     * 刷新redis中，token对应的用户信息
     */
    public boolean flushTokenData(String field, String newContent, HttpServletRequest request) {
        String tokenKey = getTokenKey(request);
        if (tokenKey != null) {
            stringRedisTemplate.opsForHash().put(tokenKey, field, newContent);
            return true;
        }
        return false;
    }

    /**
     * 删除一整个key
     */
    public boolean delete(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.delete(key));
    }

    /**
     * 删除token
     */
    public boolean deleteToken(HttpServletRequest request) {
        String tokenKey = getTokenKey(request);
        if (tokenKey != null) {
            stringRedisTemplate.delete(tokenKey);
            return true;
        }
        return false;
    }

    private String getTokenKey(HttpServletRequest request) {
        String token = request.getHeader("authorization");
        if (StrUtil.isBlank(token)) {
            return null;
        }
        return LOGIN_CODE_KEY + token;
    }
}
