package com.ccdweb.springboot.jwt;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	@Value("${jwt.secret}")
    private String secretKey;
    
    @Value("${jwt.expiration:86400000}") // 24시간 (밀리초)
    private long expiration;
    
    // Secret Key를 Key 객체로 변환
    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    // JWT 토큰 생성
    public String generateToken(String userId, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        return Jwts.builder()
                .setSubject(userId)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    // 토큰에서 사용자 ID 추출
    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        return claims.getSubject();
    }
    
    // 토큰에서 역할 추출
    public String getRoleFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        return claims.get("role", String.class);
    }
    
    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
        	Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
                
                System.out.println("Token validated successfully");
                System.out.println("   - User: " + claims.getSubject());
                System.out.println("   - Expires: " + claims.getExpiration());
                return true;
                
            } catch (ExpiredJwtException e) {
                System.out.println("❌ Token expired!!: " + e.getMessage());
                return false;
            } catch (JwtException e) {
                System.out.println("❌ Invalid token!!: " + e.getMessage());
                return false;
            } catch (Exception e) {
                System.out.println("❌ Token validation error!!: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
    }
}
