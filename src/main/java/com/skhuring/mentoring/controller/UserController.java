package com.skhuring.mentoring.controller;

import com.skhuring.mentoring.common.auth.JwtTokenProvider;
import com.skhuring.mentoring.domain.SocialType;
import com.skhuring.mentoring.domain.User;
import com.skhuring.mentoring.dto.AccessTokenDto;
import com.skhuring.mentoring.dto.GoogleProfileDto;
import com.skhuring.mentoring.dto.RedirectDto;
import com.skhuring.mentoring.service.GoogleService;
import com.skhuring.mentoring.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    private final GoogleService googleService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public UserController(GoogleService googleService, UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.googleService = googleService;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/")
    public @ResponseBody String index() {
        return "<h1>Hello SKHURing!</h1>";
    }

    @PostMapping("/google/doLogin")
    public ResponseEntity<?> googleLogin(@RequestBody RedirectDto redirectDto) {
//        액세스 토큰 발급
       AccessTokenDto accessTokenDto =  googleService.getAccessToken(redirectDto.getCode());
//       사용자 정보 얻기
       GoogleProfileDto googleProfileDto =  googleService.getGoogleProfile(accessTokenDto.getAccess_token());
//       회원가입이 되어있지 않다면 회원가입
        User originalMember = userService.getMemberBySocialId(googleProfileDto.getSub());
        if(originalMember == null){
            originalMember = userService.createOauth(googleProfileDto.getSub(), googleProfileDto.getEmail(), SocialType.GOOGLE, googleProfileDto.getName());
        }
//        회원등록되어있다면 jwttoken발급
        String jwtToken = jwtTokenProvider.createToken(originalMember.getEmail());

        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("id", originalMember.getId());
        loginInfo.put("token", jwtToken);
        loginInfo.put("name", originalMember.getName());
        return new ResponseEntity<>(loginInfo, HttpStatus.OK);
    }
}
