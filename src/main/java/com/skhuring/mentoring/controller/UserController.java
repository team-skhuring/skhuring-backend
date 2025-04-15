package com.skhuring.mentoring.controller;

import com.skhuring.mentoring.domain.Role;
import com.skhuring.mentoring.domain.SocialType;
import com.skhuring.mentoring.dto.OAuthTokenDto;
import com.skhuring.mentoring.dto.OAuthUserProfileDto;
import com.skhuring.mentoring.dto.RedirectDto;
import com.skhuring.mentoring.dto.UserDto;
import com.skhuring.mentoring.security.util.JWTUtil;
import com.skhuring.mentoring.service.GoogleService;
import com.skhuring.mentoring.service.KakaoService;
import com.skhuring.mentoring.service.UserService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor // 생성자 자동 생성 Lombok 어노테이션
public class UserController {
    private final GoogleService googleService;
    private final KakaoService kakaoService;
    private final UserService userService;

    @GetMapping("/")
    public @ResponseBody String index() {
        return "<h1>Hello SKHURing!</h1>";
    }

    @PostMapping("/google/doLogin")
    public ResponseEntity<?> googleLogin(@RequestBody RedirectDto redirectDto) {
        // 액세스 토큰 발급
       OAuthTokenDto OAuthTokenDto =  googleService.getAccessToken(redirectDto.getCode());
       // 사용자 정보 얻기
       OAuthUserProfileDto OAuthUserProfileDto =  googleService.getGoogleProfile(OAuthTokenDto.getAccessToken());
       // 회원가입이 되어있지 않다면 회원가입

        UserDto originalUser = userService.getUserBySocialId(OAuthUserProfileDto.getSub());
        if(originalUser == null){
            originalUser = userService.createOauth(OAuthUserProfileDto.getSub(), OAuthUserProfileDto.getEmail(), SocialType.GOOGLE, OAuthUserProfileDto.getName());
        }
        // 회원등록 되어있다면 jwt token 발급
        String jwtToken = JWTUtil.generateFromSocialId(originalUser.getSocialId(), SocialType.valueOf(originalUser.getSocialType().name()), Role.USER);

        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("id", originalUser.getId());
        loginInfo.put("token", jwtToken);
        loginInfo.put("name", originalUser.getName());
        return new ResponseEntity<>(loginInfo, HttpStatus.OK);
    }

    @Value("${kakao.client_id}")
    private String clientId;

    @Value("${kakao.redirect_uri}")
    private String redirectUri;

    @GetMapping("/kakao/loginStart")
    public void kakaostart(HttpServletResponse response) throws IOException {
        String kakaoUrl = "https://kauth.kakao.com/oauth/authorize?"
                + "client_id=" + clientId + "&"
                + "redirect_uri=" + redirectUri + "&"
                + "response_type=code";
        response.sendRedirect(kakaoUrl);
    }

    @PostMapping("/kakao/doLogin")
    public ResponseEntity<?> kakaoLogin(@RequestBody RedirectDto redirectDto) {
        // 액세스 토큰 발급
        String accessToken = kakaoService.getAccessToken(redirectDto.getCode());
        // 사용자 정보 조회
        OAuthUserProfileDto profile = kakaoService.getUserProfile(accessToken);

        // 회원가입 또는 조회
        UserDto userDto = userService.getUserBySocialId(profile.getSub());
        if (userDto == null) {
            userDto = userService.createOauth(profile.getSub(), profile.getEmail(), SocialType.KAKAO, profile.getName());
        }

        // JWT 토큰 발급
        String jwtToken = JWTUtil.generateFromSocialId(userDto.getSocialId(), SocialType.valueOf(userDto.getSocialType().name()), Role.USER);

        // 로그인 정보 응답
        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("id", userDto.getId());
        loginInfo.put("token", jwtToken);
        loginInfo.put("name", userDto.getName());

        return new ResponseEntity<>(loginInfo, HttpStatus.OK);
    }
}
