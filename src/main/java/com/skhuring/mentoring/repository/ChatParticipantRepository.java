package com.skhuring.mentoring.repository;

import com.skhuring.mentoring.domain.ChatParticipant;
import com.skhuring.mentoring.domain.ChatRole;
import com.skhuring.mentoring.domain.ChatRoom;
import com.skhuring.mentoring.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {
    boolean existsByChatRoomAndUser(ChatRoom chatRoom, User user);

    List<ChatParticipant> findAllByUser(User user);

    Optional<ChatParticipant> findByChatRoomAndUser(ChatRoom chatRoom, User user);

    Optional<ChatParticipant> findByChatRoomIdAndChatRole(Long chatRoomId, ChatRole chatRole);

}

