package com.jiangou.security;

import com.jiangou.common.exception.UnauthorizedException;
import com.jiangou.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private final SecretKey signingKey;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.signingKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(Long userId, String username, int tokenVersion) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtProperties.getAccessTokenExpirationMs());
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("username", username)
                .claim("type", "access")
                .claim("tv", tokenVersion)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(signingKey)
                .compact();
    }

    public String createRefreshToken(Long userId, String username, int tokenVersion) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtProperties.getRefreshTokenExpirationMs());
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("username", username)
                .claim("type", "refresh")
                .claim("tv", tokenVersion)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(signingKey)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long getUserId(String token) {
        return Long.valueOf(parseToken(token).getSubject());
    }

    public Claims validateAccessToken(String token) {
        Claims claims = parseToken(token);
        if (!"access".equals(claims.get("type"))) {
            throw new UnauthorizedException("无效的 access token");
        }
        return claims;
    }

    public Claims validateRefreshToken(String token) {
        Claims claims = parseToken(token);
        if (!"refresh".equals(claims.get("type"))) {
            throw new UnauthorizedException("无效的 refresh token");
        }
        return claims;
    }

    public Date getExpiration(String token) {
        return parseToken(token).getExpiration();
    }

    public Long requireUserIdFromBearer(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new UnauthorizedException("未登录");
        }
        return Long.valueOf(validateAccessToken(authorization.substring(7)).getSubject());
    }
}
