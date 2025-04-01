package com.skhuring.mentoring.controller;

import com.skhuring.mentoring.dto.AccessTokenDto;
import com.skhuring.mentoring.dto.RedirectDto;
import com.skhuring.mentoring.service.GoogleService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")

public class UserController {
    private final GoogleService googleService;

    public UserController(GoogleService googleService) {
        this.googleService = googleService;
    }

    @PostMapping("/google/doLogin")
    public void googleLogin(@RequestBody RedirectDto redirectDto) {
       AccessTokenDto accessTokenDto =  googleService.getAccessToken(redirectDto.getCode());
    }
}
