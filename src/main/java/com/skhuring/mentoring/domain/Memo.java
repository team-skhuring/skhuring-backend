package com.skhuring.mentoring.domain;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name = "memo")
public class Memo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_room_id")
    private Long chatRoomId;

    private String title;

    private String content;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

}
