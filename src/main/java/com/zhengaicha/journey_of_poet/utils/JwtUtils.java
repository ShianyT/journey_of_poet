package com.zhengaicha.journey_of_poet.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;

public class JwtUtils {

    private static final byte[] signKey = "zhengaicha.123".getBytes();
    private static final Long expire = 6048000000L;

    public static String generateJwt(Map<String,Object> map){
        return Jwts.builder()
                .addClaims(map)
                .signWith(SignatureAlgorithm.HS256,signKey)
                .setExpiration(new Date(System.currentTimeMillis() + expire))
                .compact();
    }

    public static Claims parseJwt(String jwt){
        return Jwts.parser()
                .setSigningKey(signKey)
                .parseClaimsJws(jwt)
                .getBody();
    }


}
