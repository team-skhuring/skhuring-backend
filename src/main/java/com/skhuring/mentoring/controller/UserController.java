package com.skhuring.mentoring.controller;

import com.skhuring.mentoring.common.auth.JwtTokenProvider;
import com.skhuring.mentoring.domain.Role;
import com.skhuring.mentoring.domain.SocialType;
import com.skhuring.mentoring.domain.User;
import com.skhuring.mentoring.dto.KakaoProfileDto;
import com.skhuring.mentoring.dto.OAuthTokenDto;
import com.skhuring.mentoring.dto.GoogleProfileDto;
import com.skhuring.mentoring.dto.RedirectDto;
import com.skhuring.mentoring.service.GoogleService;
import com.skhuring.mentoring.service.KakaoService;
import com.skhuring.mentoring.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor // 생성자 자동 생성 Lombok 어노테이션
public class UserController {
    private final UserService userService;
    private final GoogleService googleService;
    private final KakaoService kakaoService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/google/doLogin")
    public ResponseEntity<?> googleLogin(@RequestBody RedirectDto redirectDto) {
//        액세스 토큰 발급
       OAuthTokenDto OAuthTokenDto =  googleService.getAccessToken(redirectDto.getCode());
//       사용자 정보 얻기
       GoogleProfileDto GoogleProfileDto =  googleService.getGoogleProfile(OAuthTokenDto.getAccessToken());
//       회원가입이 되어있지 않다면 회원가입
        User originalMember = userService.getMemberBySocialId(GoogleProfileDto.getSub());
        log.info("original member: {}", originalMember);
        if(originalMember == null){
            originalMember = userService.createOauth(GoogleProfileDto.getSub(), GoogleProfileDto.getEmail(), SocialType.GOOGLE, GoogleProfileDto.getName(), null, Role.USER);
        }
//        회원등록되어있다면 jwttoken발급
        String jwtToken = jwtTokenProvider.createToken(originalMember.getSocialId(), Role.USER);

        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("id", originalMember.getId());
        loginInfo.put("token", jwtToken);
        loginInfo.put("name", originalMember.getName());
        return new ResponseEntity<>(loginInfo, HttpStatus.OK);
    }

    @Value("${kakao.client_id}")
    private String clientId;

    @Value("${kakao.redirect_uri}")
    private String redirectUri;

    @PostMapping("/kakao/doLogin")
    public ResponseEntity<?> kakaoLogin(@RequestBody RedirectDto redirectDto) {
        // 액세스 토큰 발급
        String accessToken = kakaoService.getAccessToken(redirectDto.getCode());
        // 사용자 정보 조회
        KakaoProfileDto profile = kakaoService.getUserProfile(accessToken);

        // 회원가입 또는 조회
        User originalMember = userService.getMemberBySocialId(profile.getId());
        if (originalMember == null) {
            originalMember = userService.createOauth(
                    profile.getId(),                                         // socialId
                    profile.getKakao_account().getEmail(),                   // email
                    SocialType.KAKAO,
                    profile.getKakao_account().getProfile().getNickname(),   // name
                    profile.getKakao_account().getProfile().getProfile_image_url(), // profileImage
                    Role.USER
            );
        }

        // JWT 토큰 발급
        String jwtToken = jwtTokenProvider.createToken(originalMember.getSocialId(), Role.USER);

        // 로그인 정보 응답
        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("id", originalMember.getId());
        loginInfo.put("token", jwtToken);
        loginInfo.put("name", originalMember.getName());

        return new ResponseEntity<>(loginInfo, HttpStatus.OK);
    }
}
