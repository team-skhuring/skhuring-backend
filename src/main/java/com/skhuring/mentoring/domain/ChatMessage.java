package com.skhuring.mentoring.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name = "chat_message")
public class ChatMessage extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "send_user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageType messageType;

}
