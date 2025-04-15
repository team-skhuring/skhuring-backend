package com.skhuring.mentoring.repository;

import com.skhuring.mentoring.domain.SocialType;
import com.skhuring.mentoring.domain.User;
import com.skhuring.mentoring.dto.UserDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findBySocialId(String socialId);
    Optional<UserDto> findBySocialIdAndSocialType(String socialId, SocialType socialType);
}
