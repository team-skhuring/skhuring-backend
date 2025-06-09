package com.skhuring.mentoring.repository;

import com.skhuring.mentoring.domain.ChatMessage;
import com.skhuring.mentoring.domain.ChatRoom;
import com.skhuring.mentoring.dto.ChatRoomWithUnreadDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomOrderByCreateTime(ChatRoom chatRoom);

    @Query("SELECT m FROM ChatMessage m WHERE m.chatRoom.id = :roomId AND :userId NOT MEMBER OF m.readers")
    List<ChatMessage> findUnreadMessages(@Param("roomId") Long roomId, @Param("userId") Long userId);

    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.chatRoom.id = :roomId AND :userId NOT MEMBER OF m.readers")
    int countUnreadMessages(@Param("roomId") Long roomId, @Param("userId") Long userId);

    @Query("""
    SELECT new com.skhuring.mentoring.dto.ChatRoomWithUnreadDto(
        cp.chatRoom.id,
        cp.chatRoom.title,
        (
            SELECT cm.content
            FROM ChatMessage cm
            WHERE cm.chatRoom = cp.chatRoom
            AND cm.createTime = (
                SELECT MAX(cm3.createTime)
                FROM ChatMessage cm3
                WHERE cm3.chatRoom = cp.chatRoom
            )
        ),
        (
            SELECT COUNT(cm2)
            FROM ChatMessage cm2
            WHERE cm2.chatRoom = cp.chatRoom
            AND :userId NOT MEMBER OF cm2.readers
        )
    )
    FROM ChatParticipant cp
    WHERE cp.user.id = :userId
""")
    List<ChatRoomWithUnreadDto> findChatRoomsWithUnreadCountAndRecentMessage(@Param("userId") Long userId);





}
