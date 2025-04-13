package com.skhuring.mentoring.service;

import com.skhuring.mentoring.dto.OAuthTokenDto;
import com.skhuring.mentoring.dto.OAuthUserProfileDto;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
@Transactional
public class GoogleService {
    @Value("${oauth.google.client-id}")
    private String googleClientId;
    @Value("${oauth.google.client-secret}")
    private String googleClientSecret;
    @Value("${oauth.google.redirect-uri}")
    private String googleRedirectUri;

    public OAuthTokenDto getAccessToken(String code) {
        // MultiValueMap 을 통해 자동으로 form-data 형식으로 body 조립 가능
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("redirect_uri", googleRedirectUri);
        params.add("grant_type", "authorization_code");

        ResponseEntity<OAuthTokenDto> response =  RestClient.create().post()
                .uri("https://oauth2.googleapis.com/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(params)
                .retrieve()
                .toEntity(OAuthTokenDto.class);
        log.info("[Google AccessToken] {}", response.getBody());
        return response.getBody();
    }

    public OAuthUserProfileDto getGoogleProfile(String accessToken) {
        ResponseEntity<OAuthUserProfileDto> response = RestClient.create().get()
                .uri("https://openidconnect.googleapis.com/v1/userinfo")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .toEntity(OAuthUserProfileDto.class);
        log.info("[Google Profile] {}", response.getBody());

        OAuthUserProfileDto profile = response.getBody();
        return new OAuthUserProfileDto(profile.getSub(), profile.getEmail(), profile.getName(), "google");
    }


}
