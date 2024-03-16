package com.zhengaicha.journey_of_poet.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhengaicha.journey_of_poet.dto.UserDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expireTime}")
    private Long expireTime;

    ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 生成token
     *
     * @param payload 载荷对象
     * @return token
     */
    public String createToken(UserDTO payload) throws JsonProcessingException {
        long l = System.currentTimeMillis() + (expireTime * 3600 * 24 * 2400);
        return JWT.create()
                .withSubject(objectMapper.writeValueAsString(payload))
                .withClaim("expireTime",l)
                .sign(Algorithm.HMAC256(secret));
    }

    /**
     * 判断jwt令牌是否过期
     * ture为已过期
     */
    public boolean isJwtExpire(String jwt){
        DecodedJWT verify = JWT.require(Algorithm.HMAC256(secret)).build().verify(jwt);
        Long expireTime1 = verify.getClaim("expireTime").asLong();
        long time = System.currentTimeMillis();
        return (expireTime1 - time < 0);

    }

    /**
     * 解析jwt
     * @param jwt 用户带入的jwt令牌
     * @return UserDTO对象
     */
    public UserDTO parseJwt(String jwt) {
        try {
            DecodedJWT verify = null;
            try {
                verify = JWT.require(Algorithm.HMAC256(secret)).build().verify(jwt);
            } catch (JWTVerificationException | IllegalArgumentException e) {
                return null;
            }
            String payload = verify.getSubject();
            return objectMapper.readValue(payload, UserDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


}
