package com.skhuring.mentoring.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name = "chat_room")
public class ChatRoom extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(nullable = false)
    private boolean anonymous;

    private static final int MAX_PARTICIPANTS = 4;

    @ManyToOne(fetch = FetchType.LAZY)  // ManyToOne 관계 설정
    @JoinColumn(name = "creator_id", nullable = false) // creator_id 외래키 설정
    private User creator;

    private int currentMemberCount;

    private boolean mentor_status;

    private String room_status; // "ACTIVE", "CLOSED"

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE)
    private List<ChatParticipant> chatParticipantList = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE)
    private List<ChatMessage> chatMessageList = new ArrayList<>();

    public void checkMentorJoined() {
        this.mentor_status = true;
    }

    public void increaseCurrentMemberCount() {
        this.currentMemberCount++;
    }

    public void decreaseCurrentMemberCount() {
        this.currentMemberCount--;
    }

    public void closeRoom() {
        this.room_status = "CLOSED";
    }
}
