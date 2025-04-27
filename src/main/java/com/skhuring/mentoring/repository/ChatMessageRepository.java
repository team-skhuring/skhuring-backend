package com.skhuring.mentoring.repository;

import com.skhuring.mentoring.domain.ChatMessage;
import com.skhuring.mentoring.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomOrderByCreateTime(ChatRoom chatRoom);
}
