package com.skhuring.mentoring.dto;

import com.skhuring.mentoring.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResDto {
    private String id;
    private String name;
    private String email;
    private String socialId;
    private String socialType;
    private String profileImage;
    private int point;

    /* DTO 변환을 위한 정적 팩토리 메소드 */
    public static UserResDto from(User user) {
        return UserResDto.builder()
                .id(String.valueOf(user.getId()))
                .name(user.getName())
                .email(user.getEmail())
                .socialId(user.getSocialId())
                .socialType(user.getSocialType().name())
                .profileImage(user.getProfileImage())
                .point(user.getPoint())
                .build();
    }
}
