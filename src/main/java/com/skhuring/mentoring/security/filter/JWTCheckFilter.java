package com.skhuring.mentoring.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JWTCheckFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

    }

    /* 토큰없이 수락해주어야 하는 요청들 설정 */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        log.info("check uri : {}", path);

        /* 요청 url 이 아래와 같이 시작되면 토큰 필터링 없이 요청을 수락함 */
        if(request.getMethod().equals("OPTIONS")) return true; // 클라이언트가 요청을 보내기 전, 서버 지원 정보 확인용
        if(path.startsWith("/user/")) return true; // 테스트
        if(path.startsWith("/user/kakao")) return true;
        if(path.startsWith("/user/google")) return true;

        //if(path.startsWith("/user/login")) return true;

        return false;
    }
}
