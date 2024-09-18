package com.atguigu.lease.common.utils;

import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.result.ResultCodeEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {

    private static SecretKey secretKey = Keys.hmacShaKeyFor("oUTzaoUGmKOzdHFx9eDSSZqtY32nugV6".getBytes()); //密钥

    // 创建token
    public static String createToken(Long userId, String username) {
        String jwt = Jwts.builder() //创建jwt工厂
                .setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000)) //设置过期时间
                .setSubject("Login_User") //设置主题
                .claim("userId", userId) //设置用户id"
                .claim("username", username) //设置用户名
                .signWith(secretKey, SignatureAlgorithm.HS256)//设置加密方式
                .compact();
        return jwt;
    }

    // 解析token
    public static Claims parseTaken(String token) {

        if (token == null) {
            throw new LeaseException(ResultCodeEnum.ADMIN_LOGIN_AUTH);
        }

        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                                        .setSigningKey(secretKey)
                                        .build()
                                        .parseClaimsJws(token);
            return claimsJws.getBody();
        } catch (ExpiredJwtException e) {
            throw new LeaseException(ResultCodeEnum.TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new LeaseException(ResultCodeEnum.TOKEN_INVALID);
        }
    }

    public static void main(String[] args) {
        System.out.println(createToken(1L, "admin"));
    }
}
