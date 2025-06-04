package com.skhuring.mentoring.repository;
import com.skhuring.mentoring.domain.ChatRoom;
import com.skhuring.mentoring.dto.ChatRoomWithUnreadDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT cr FROM ChatRoom cr JOIN cr.chatParticipantList p WHERE p.user.id = :userId")
    List<ChatRoom> findChatRoomsByUserId(@Param("userId") Long userId);


}
