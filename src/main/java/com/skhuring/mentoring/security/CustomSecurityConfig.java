package com.skhuring.mentoring.security;

import com.skhuring.mentoring.security.filter.JWTCheckFilter;
import com.skhuring.mentoring.security.handler.CustomAccessDeniedHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CustomSecurityConfig {

    private final JWTCheckFilter jwtCheckFilter;

    /* security 환경 설정을 위한 Bean
    * security 시스템이 발동 후 가장 먼저 찾아 실행하는 메소드(Bean)
    * security Config 를 전체적으로 설정 */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        log.info("[Configuring Custom Security Filter Chain]");

        /* CORS 제약 설정 */
        http.cors(
                httpSecurityCorsConfigurer -> {
                    httpSecurityCorsConfigurer.configurationSource( corsConfigurationSource() );
                }
        );

        /* CSRF(Cross-Site Request Forgery)
        * 신뢰할 수 있는 사용자를 사칭해 웹사이트에 원하지 않는 명령을 보내는 공격
        * 일정 조건이 맞는 사람의 공격에 대한 경로를 없애버림 */
        http.csrf(AbstractHttpConfigurer::disable);

        /* 세션에 상태 저장을 하지 않을 환경 설정 */
        http.sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        /* SNS 로그인 라우트 허용 */
        http.authorizeHttpRequests(authz -> authz
                .requestMatchers(
                        "/user/kakao/**",
                        "/user/google/**"
                ).permitAll() // 인증 없이 접근 허용
                .anyRequest().authenticated()
        );

        /* JWT 액세스 토큰 체크
        * 요청 토큰을 어디서 체크하고 검증할건지에 대한 설정
        * 토큰 발급은 APILoginSuccessHandler() 에서 발급
        * 이후 발급된 토큰으로 다음 정보를 요청할 때 토큰의 유효성을 체크하는 환경 */
        http.addFilterBefore(jwtCheckFilter, UsernamePasswordAuthenticationFilter.class);

        /* 접근 시 발생한 로그인 이외의 모든 예외 처리 (액세스 토큰 오류, 로그인 오류 등) 에 대한 설정 */
        http.exceptionHandling(
                config -> {
                    config.accessDeniedHandler(new CustomAccessDeniedHandler());
                }
        );

        return http.build();
    }

    /* CORS 설정을 위한 Bean
    * CORS 제약 설정의 자세한 사항
    * 브라우저에서 cross origin 요청을 안전하게 할 수 있도록 하는 매커니즘 */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration(); // 새로운 CorsConfiguration 을 생성 후 규칙을 추가하고 리턴 (정책 설정)
        //configuration.setAllowedOriginPatterns(Arrays.asList("*")); // 모든 아이피(출발 지점)에 대해 응답 허용
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE")); // 해당 요청에만 응답 허용
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type")); // 해당 헤더에 대해서만 응답 허용
        configuration.setAllowCredentials(true); // 서버가 응답할 때 json 을 JS 에서 처리할 수 있게 설정

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(); // 현재 설정사항 등 웹에 필요한 CORS 환경설정 클래스에 추가하여 리턴
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /* password 암호화를 위한 Bean
    * 전송된 비밀번호를 passwordEncorder 를 이용하여 암호화 */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
