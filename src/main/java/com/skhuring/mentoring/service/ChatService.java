package com.skhuring.mentoring.service;

import com.skhuring.mentoring.domain.*;
import com.skhuring.mentoring.dto.ChatMessageReqDto;
import com.skhuring.mentoring.dto.CreateChatRoomReqDto;
import com.skhuring.mentoring.repository.ChatMessageRepository;
import com.skhuring.mentoring.repository.ChatParticipantRepository;
import com.skhuring.mentoring.repository.ChatRoomRepository;
import com.skhuring.mentoring.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatParticipantRepository chatParticipantRepository;

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

    public void createRoom(CreateChatRoomReqDto request) {
        Category categoryEnum;
        try {
            categoryEnum = Category.valueOf(request.getCategory().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("잘못된 카테고리입니다.");
        }
        User user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(()-> new EntityNotFoundException("user cannot be found"));
//        채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .title(request.getTitle())
                .category(categoryEnum)
                .anonymous(request.isAnonymous())
                .build();
        chatRoomRepository.save(chatRoom);
//        채팅방 개설자는 바로 채팅방 참여시킴
        ChatParticipant chatParticipant = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .user(user)
                .build();
        chatParticipantRepository.save(chatParticipant);
    }


}
