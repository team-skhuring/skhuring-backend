package com.skhuring.mentoring.service;

import com.skhuring.mentoring.domain.SocialType;
import com.skhuring.mentoring.domain.User;
import com.skhuring.mentoring.dto.UserDto;
import com.skhuring.mentoring.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserDto getUserBySocialId(String socialId) {
        return userRepository.findBySocialId(socialId)
                .map(UserDto::from)
                .orElse(null);
    }

    public UserDto createOauth(String socialId, String email, SocialType socialType, String name) {
        User user = User.builder()
                .name(name)
                .email(email)
                .socialType(socialType)
                .socialId(socialId)
                .build();

        userRepository.save(user);
        return UserDto.from(user);
    }

    public Optional<UserDto> findBySocialIdAndSocialType(String socialId, SocialType socialType) {
        return userRepository.findBySocialIdAndSocialType(socialId, socialType);
    }
}
