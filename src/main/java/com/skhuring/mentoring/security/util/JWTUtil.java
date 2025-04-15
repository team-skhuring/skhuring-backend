package com.skhuring.mentoring.security.util;

import com.skhuring.mentoring.domain.Role;
import com.skhuring.mentoring.domain.SocialType;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.InvalidClaimException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JWTUtil {
    /* 토큰 발급에 필요한 30자리 이상의 키값 문자열 */
    private static String key = "1234567890123456789012345678901234567890";

    /* socialId 기반 AccessToekn 생성 요청 */
    public static String generateFromSocialId(String sub, SocialType socialType, Role role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", sub);
        claims.put("socialType", socialType);
        claims.put("role", role);
        return generateJWT(claims, 60);
    }

    /* AccessToken 생성 */
    public static String generateJWT(Map<String, Object> claims, int min) {
        SecretKey key = null;
        try {
            key = Keys.hmacShaKeyFor(JWTUtil.key.getBytes("UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        String accessToken = Jwts.builder()
                .setHeader(Map.of("typ", "JWT"))
                .setClaims(claims)
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(min).toInstant()))
                .signWith(key)
                .compact();
        log.info("[accessToken] {}", accessToken);
        return accessToken;
    }

    private static SecretKey createSecretKey() {
        try {
            return Keys.hmacShaKeyFor(key.getBytes("UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /* 토큰 유효성 검사 */
    public static Map<String, Object> validateToken(String token) throws CustomJWTException {
        Map<String, Object> claims = null;
        SecretKey key = null;
        try {
            key = Keys.hmacShaKeyFor(JWTUtil.key.getBytes("utf-8"));
            claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            log.info("[check claims] {}", claims);
        } catch (ExpiredJwtException expiredJwtException) {
            throw new CustomJWTException("Expired");
        } catch (InvalidClaimException invalidClaimException) {
            throw new CustomJWTException("Invalid");
        } catch (JwtException jwtException) {
            throw new CustomJWTException("JWTError");
        } catch (Exception e) {
            throw new CustomJWTException("Error");
        }
        return claims;
    }
}
