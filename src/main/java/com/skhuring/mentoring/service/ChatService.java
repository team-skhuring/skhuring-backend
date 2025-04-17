package com.skhuring.mentoring.service;

import com.skhuring.mentoring.domain.ChatMessage;
import com.skhuring.mentoring.domain.ChatRoom;
import com.skhuring.mentoring.domain.MessageType;
import com.skhuring.mentoring.domain.User;
import com.skhuring.mentoring.dto.ChatMessageReqDto;
import com.skhuring.mentoring.repository.ChatMessageRepository;
import com.skhuring.mentoring.repository.ChatRoomRepository;
import com.skhuring.mentoring.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;

    public void saveMessage(Long roomId, ChatMessageReqDto chatMessageReqDto) {
//        채팅방조회
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(()-> new EntityNotFoundException("room cannot be found"));
//        보낸사람조회
        User user = userRepository.findByName(chatMessageReqDto.getSender()).orElseThrow(()-> new EntityNotFoundException("user cannot be found"));

//        메시지저장
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .user(user)
                .content(chatMessageReqDto.getContent())
                .messageType(MessageType.TEXT)
                .build();
        chatMessageRepository.save(chatMessage);

    }


}
