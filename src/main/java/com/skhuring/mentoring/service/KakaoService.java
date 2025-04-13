package com.skhuring.mentoring.service;

import com.skhuring.mentoring.dto.OAuthTokenDto;
import com.skhuring.mentoring.dto.OAuthUserProfileDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {

    @Value("${kakao.client_id}")
    private String clientId;

    @Value("${kakao.redirect_uri}")
    private String redirectUri;

    private final WebClient webClient = WebClient.create("https://kauth.kakao.com");

    public String getAccessToken(String code) {
        OAuthTokenDto token = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/oauth/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", clientId)
                        .queryParam("redirect_uri", redirectUri)
                        .queryParam("code", code)
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, res -> Mono.error(new RuntimeException("잘못된 요청")))
                .onStatus(HttpStatusCode::is5xxServerError, res -> Mono.error(new RuntimeException("카카오 서버 오류")))
                .bodyToMono(OAuthTokenDto.class)
                .block();

        log.info("[Kakao AccessToken] {}", token.getAccessToken());
        return token.getAccessToken();
    }

    public OAuthUserProfileDto getUserProfile(String accessToken) {
        OAuthUserProfileDto profile = WebClient.create("https://kapi.kakao.com").get()
                .uri("/v2/user/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(OAuthUserProfileDto.class)
                .block();

        return new OAuthUserProfileDto(profile.getSub(), profile.getEmail(), profile.getName(), "kakao");
    }
}
