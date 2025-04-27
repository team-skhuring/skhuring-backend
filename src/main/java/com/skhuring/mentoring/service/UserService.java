package com.skhuring.mentoring.service;

import com.skhuring.mentoring.domain.Role;
import com.skhuring.mentoring.domain.SocialType;
import com.skhuring.mentoring.domain.User;
import com.skhuring.mentoring.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @GetMapping("/")
    public @ResponseBody String index() {
        return "<h1>Hello SKHURing!</h1>";
    }

    public User getMemberBySocialId(String socialId){
        User user = userRepository.findBySocialId(socialId).orElse(null);
        return user;
    }

    /* SNS 회원가입 */
    public User createOauth(String socialId, String email, SocialType socialType, String name, String profileImage, Role role){
        User user = User.builder()
                .email(email)
                .socialType(socialType)
                .socialId(socialId)
                .role(role)
                .name(name)
                .profileImage(profileImage)
                .build();
        userRepository.save(user);
        return user;
    }
}
