package com.yihu.common;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {
    // 从配置读取（示例硬编码，实际项目建议用@Value注入）
    private static final String SECRET = "your-256-bit-secret-key-here-1234567890";
    private static final long EXPIRATION = 86400L; // 24小时（单位：秒）

    // 生成 Token
    public static String generateToken(String phone, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("phone", phone);

        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(phone)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION * 1000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 解析 Token（后续鉴权用）
    public static Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}