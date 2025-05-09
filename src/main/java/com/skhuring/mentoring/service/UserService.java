package com.skhuring.mentoring.service;

import com.skhuring.mentoring.domain.Role;
import com.skhuring.mentoring.domain.SocialType;
import com.skhuring.mentoring.domain.User;
import com.skhuring.mentoring.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

@Service
@Transactional
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

    /* id 기반 로그인 유저 정보 가져오기 */
    public Optional<User> getLoginInfo(long userId) {
        return userRepository.findById(userId);
    }
}
