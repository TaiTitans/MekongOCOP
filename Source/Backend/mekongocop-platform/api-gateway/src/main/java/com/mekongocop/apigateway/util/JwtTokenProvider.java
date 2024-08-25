package com.mekongocop.apigateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class JwtTokenProvider {

    private final String jwtSecret;
    private final long jwtAccessTokenExpirationInMs;
    private final long jwtRefreshTokenExpirationInMs;

    public JwtTokenProvider(
            @Value("${app.jwt.secret}") String jwtSecret,
            @Value("${app.jwt.access-token-expiration-in-ms}") long jwtAccessTokenExpirationInMs,
            @Value("${app.jwt.refresh-token-expiration-in-ms}") long jwtRefreshTokenExpirationInMs
    ) {
        this.jwtSecret = jwtSecret;
        this.jwtAccessTokenExpirationInMs = jwtAccessTokenExpirationInMs;
        this.jwtRefreshTokenExpirationInMs = jwtRefreshTokenExpirationInMs;
    }

    public String generateAccessToken(String userId, String username, List<String> roles) {
        return Jwts.builder()
                .setSubject(userId)
                .claim("username", username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtAccessTokenExpirationInMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String generateRefreshToken(String userId, String username, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", userId);
        claims.put("username", username);
        claims.put("roles", roles);
        return doGenerateToken(claims, userId, jwtRefreshTokenExpirationInMs);
    }

    private String doGenerateToken(Map<String, Object> claims, String subject, long expirationInMs) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationInMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUserIdFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject();
    }

    public List<String> getRolesFromToken(String token) {
        Claims claims = parseClaims(token);
        return (List<String>) claims.get("roles");
    }
    public Jwt validateAndParseToken(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();

            // Tạo một Map cho header
            Map<String, Object> headers = new HashMap<>();
            headers.put("alg", "HS512");  // Giả sử bạn đang sử dụng HS512
            headers.put("typ", "JWT");

            return new Jwt(token,
                    claims.getIssuedAt().toInstant(),
                    claims.getExpiration().toInstant(),
                    headers,
                    claims);
        } catch (Exception e) {
            return null;
        }
    }
}
