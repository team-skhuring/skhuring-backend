package com.skhuring.mentoring.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skhuring.mentoring.domain.Role;
import com.skhuring.mentoring.domain.SocialType;
import com.skhuring.mentoring.dto.UserDto;
import com.skhuring.mentoring.security.util.CustomJWTException;
import com.skhuring.mentoring.security.util.JWTUtil;
import com.skhuring.mentoring.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTCheckFilter extends OncePerRequestFilter {

    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.info("[JWTFilter running]");
        String authHeader = request.getHeader("Authorization");
        log.info("[authHeader] {}", authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // JWT 파싱
            String token = authHeader.substring(7);
            Map<String, Object> claims = JWTUtil.validateToken(token);
            log.info("[JWT claims] {}", claims);

            // 토큰 클레임 추출
            String sub = (String) claims.get("sub");
            SocialType socialType = SocialType.valueOf((String) claims.get("socialType"));
            Role role = Role.valueOf((String) claims.get("role"));

            // 사용자 정보 조회 & 인증 등록
            UserDto userDto = (UserDto) userService.findBySocialIdAndSocialType(sub, socialType)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userDto, null, userDto.getAuthorities());

            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            new ObjectMapper().writeValue(response.getWriter(), Map.of("error", "INVALID_TOKEN"));
        } catch (CustomJWTException e) {
            throw new RuntimeException(e);
        }
    }

    /* 토큰없이 수락해주어야 하는 요청들 설정 */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        log.info("[check uri] {}", path);

        /* 요청 url 이 아래와 같이 시작되면 토큰 필터링 없이 요청을 수락함 */
        if(request.getMethod().equals("OPTIONS")) return true; // 클라이언트가 요청을 보내기 전, 서버 지원 정보 확인용
        //if(path.startsWith("/user/")) return true; // 테스트
        if(path.startsWith("/user/kakao")) return true;
        if(path.startsWith("/user/google")) return true;
        if (path.startsWith("/api/user/google")) return true;
        if (path.startsWith("/api/user/kakao")) return true;

        return false;
    }
}
