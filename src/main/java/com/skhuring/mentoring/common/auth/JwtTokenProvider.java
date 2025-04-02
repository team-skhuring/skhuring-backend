package com.skhuring.mentoring.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final String secretKey;
    private final int expiration;
    private Key SECRET_KEY;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, @Value("${jwt.expiration}") int expiration) {
        this.secretKey = secretKey;
        this.expiration = expiration;
        this.SECRET_KEY = new SecretKeySpec(secretKey.getBytes(), SignatureAlgorithm.HS512.getJcaName());
    }

    public String createToken(String email){
//        claims는 jwt토큰의 payload부분을 의미
        Claims claims = Jwts.claims().setSubject(email);
        Date now = new Date();
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+ expiration*60*1000L))
                .signWith(SECRET_KEY)
                .compact();
        return token;
    }



}
