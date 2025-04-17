package com.skhuring.mentoring.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email; //카카오는 이메일이 안된다고 했음 일단 넣어둠

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "social_type", nullable = false)
    private SocialType socialType;

    @Column(name = "social_id", nullable = false, unique = true)
    private String socialId;

    @Column(name = "profile_image")
    private String profileImage;

}
