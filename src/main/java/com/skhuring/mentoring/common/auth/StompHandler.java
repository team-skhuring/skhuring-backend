package com.skhuring.mentoring.common.auth;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

@Component
@Slf4j
public class StompHandler implements ChannelInterceptor {

    @Value("${jwt.secret}")
    private String secret;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if(StompCommand.CONNECT.equals(accessor.getCommand())) {
            log.info("Connect 요청시 토큰 유효성 검증");
            String bearerToken = accessor.getFirstNativeHeader("Authorization");
            String token = bearerToken.substring( 7);
            log.info("token : {}", token);
//          토큰검증
            try {
                Key key = new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS512.getJcaName()); // 추가
                Jwts.parserBuilder()
                        .setSigningKey(key) // ✨ 문자열(secret)이 아니라 Key로 검증
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
                log.info("token검증 완료");
            } catch (Exception e) {
                log.error("JWT 파싱 실패: {}", e.getMessage(), e);
            }
        }
        return message;
    }



}
