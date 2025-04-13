package com.skhuring.mentoring.service;

import com.skhuring.mentoring.domain.SocialType;
import com.skhuring.mentoring.domain.User;
import com.skhuring.mentoring.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getUserBySocialId(String socialId){
        User user = userRepository.findBySocialId(socialId).orElse(null);
        return user;
    }

    public User createOauth(String socialId, String email, SocialType socialType, String name){
        User user = User.builder()
                .email(email)
                .socialType(socialType)
                .socialId(socialId)
                .name(name)
                .build();
        userRepository.save(user);
        return user;
    }



}
