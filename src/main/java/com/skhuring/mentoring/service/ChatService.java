package com.skhuring.mentoring.service;

import com.skhuring.mentoring.domain.*;
import com.skhuring.mentoring.dto.ChatMessageReqDto;
import com.skhuring.mentoring.dto.ChatRoomListResDto;
import com.skhuring.mentoring.dto.CreateChatRoomReqDto;
import com.skhuring.mentoring.dto.JoinChatRoomReqDto;
import com.skhuring.mentoring.repository.ChatMessageRepository;
import com.skhuring.mentoring.repository.ChatParticipantRepository;
import com.skhuring.mentoring.repository.ChatRoomRepository;
import com.skhuring.mentoring.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
                .creator_name(user.getName())
                .anonymous(request.isAnonymous())
                .currentMemberCount(1)
                .mentor_status(false)
                .build();
        chatRoomRepository.save(chatRoom);
//        채팅방 개설자는 바로 채팅방 참여시킴
        ChatParticipant chatParticipant = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .user(user)
                .build();
        chatParticipantRepository.save(chatParticipant);
    }


    public List<ChatRoomListResDto> getChatRoomsList() {
        List<ChatRoom> chatRoomList = chatRoomRepository.findAll();

        List<ChatRoomListResDto> dtos = new ArrayList<>();
        for (ChatRoom chatRoom : chatRoomList) {
            ChatRoomListResDto dto = ChatRoomListResDto.builder()
                    .roomId(chatRoom.getId())
                    .title(chatRoom.getTitle())
                    .creatorName(chatRoom.getCreator_name())
                    .category(String.valueOf(chatRoom.getCategory()))
                    .isFull(false)
                    .mentor_status(chatRoom.isMentor_status())
                    .currentMemberCount(chatRoom.getCurrentMemberCount())
                    .build();
            dtos.add(dto);
        }
        return dtos;
    }

    public void addParticipantToChatRoom(JoinChatRoomReqDto request) {
        User user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        ChatRoom chatRoom = chatRoomRepository.findById(Long.valueOf(request.getRoomId()))
                .orElseThrow(() -> new EntityNotFoundException("채팅방을 찾을 수 없습니다."));

        // 참가자 추가
        ChatParticipant participant = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .user(user)
                .build();
        chatParticipantRepository.save(participant);

        // 멘토일 경우 채팅방 상태 업데이트
        if ("MENTOR".equalsIgnoreCase(request.getRole()) && !chatRoom.isMentor_status()) {
            chatRoom.checkMentorJoined();
            chatRoomRepository.save(chatRoom);
        }


    }
}
