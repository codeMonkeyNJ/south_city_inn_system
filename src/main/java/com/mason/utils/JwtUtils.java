package com.mason.utils;

import java.util.Map;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;

public class JwtUtils {

    private static String signKey;//密钥
    @Value("${jwt.sign-key}")
    public static void setSignKey(String signKey) {
        JwtUtils.signKey = signKey;
    }

    /**
     * 生成JWT令牌
     * @param claims 自定义属性
     * @return JWT令牌
     */
    public static String generateJwt(Map<String,Object> claims){
        return Jwts.builder()
                .addClaims(claims)//添加自定义属性
                .signWith(SignatureAlgorithm.HS256, signKey)//签名算法
                .compact();
    }

    /**
     * 解析JWT令牌
     * @param jwt JWT令牌
     * @return JWT第二部分负载 payload 中存储的内容
     */
    public static Claims parseJWT(String jwt){
        return Jwts.parser()
                .setSigningKey(signKey)
                .parseClaimsJws(jwt)
                .getBody();
    }

}
