package com.skhuring.mentoring.service;

import com.skhuring.mentoring.domain.*;
import com.skhuring.mentoring.dto.*;
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
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatParticipantRepository chatParticipantRepository;

    public void saveMessage(Long roomId, ChatMessageDto chatMessageReqDto) {
//        채팅방조회
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(()-> new EntityNotFoundException("room cannot be found"));
//        보낸사람조회
        User user = userRepository.findByName(chatMessageReqDto.getSender()).orElseThrow(()-> new EntityNotFoundException("user cannot be found"));

//        메시지저장
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .user(user)
                .content(chatMessageReqDto.getContent())
                .messageType(chatMessageReqDto.getMessageType())
                .build();
        chatMessageRepository.save(chatMessage);

    }

    public Long createRoom(CreateChatRoomReqDto request) {
        Category categoryEnum;
        try {
            categoryEnum = Category.valueOf(request.getCategory().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("잘못된 카테고리입니다.");
        }
        User user = userRepository.findBySocialId(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(()-> new EntityNotFoundException("user cannot be found"));
//        채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .title(request.getTitle())
                .category(categoryEnum)
                .creator(user)
                .anonymous(request.isAnonymous())
                .currentMemberCount(1)
                .mentor_status(false)
                .build();
        chatRoomRepository.save(chatRoom);
//        채팅방 개설자는 바로 채팅방 참여시킴
        ChatParticipant chatParticipant = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .user(user)
                .chatRole(ChatRole.valueOf("MENTEE"))
                .build();
        chatParticipantRepository.save(chatParticipant);

        return chatRoom.getId();
    }


    public List<ChatRoomListResDto> getChatRoomsList() {
        List<ChatRoom> chatRoomList = chatRoomRepository.findAll();

        List<ChatRoomListResDto> dtos = new ArrayList<>();
        for (ChatRoom chatRoom : chatRoomList) {
            // "CLOSED" 상태인 채팅방은 제외
            if (!"CLOSED".equals(chatRoom.getRoom_status())) {
                ChatRoomListResDto dto = ChatRoomListResDto.builder()
                        .roomId(chatRoom.getId())
                        .title(chatRoom.getTitle())
                        .creatorName(chatRoom.getCreator().getName())
                        .category(String.valueOf(chatRoom.getCategory()))
                        .isFull(false)
                        .mentor_status(chatRoom.isMentor_status())
                        .currentMemberCount(chatRoom.getCurrentMemberCount())
                        .build();
                dtos.add(dto);
            }
        }
        return dtos;
    }

    public void addParticipantToChatRoom(JoinChatRoomReqDto request) {
        User user = userRepository.findBySocialId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        ChatRoom chatRoom = chatRoomRepository.findById(Long.valueOf(request.getRoomId()))
                .orElseThrow(() -> new EntityNotFoundException("채팅방을 찾을 수 없습니다."));

        boolean alreadyParticipant = chatParticipantRepository.existsByChatRoomAndUser(chatRoom, user);
        if (alreadyParticipant) {
            throw new IllegalStateException("이미 채팅방에 참가한 사용자입니다.");
        }

        chatRoom.increaseCurrentMemberCount();
        chatRoomRepository.save(chatRoom);

        // 참가자 추가
        ChatParticipant participant = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .user(user)
                .chatRole(ChatRole.valueOf(request.getRole()))
                .build();
        chatParticipantRepository.save(participant);

        // 멘토일 경우 채팅방 상태 업데이트
        if ("MENTOR".equalsIgnoreCase(request.getRole()) && !chatRoom.isMentor_status()) {
            chatRoom.checkMentorJoined();
            chatRoomRepository.save(chatRoom);
        }


    }

    public List<ChatMessageDto> getChatHistory(String roomId) {
//        내가 해당 채팅방의 참여자가 아닐경우 에러
        ChatRoom chatRoom = chatRoomRepository.findById(Long.valueOf(roomId))
                .orElseThrow(() -> new EntityNotFoundException("채팅방을 찾을 수 없습니다."));
        User user = userRepository.findBySocialId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

      /*
        List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);
        boolean check = false;
        for (ChatParticipant c : chatParticipants) {
            if(c.getUser().equals(user)) {
                check = true;
            }
        }
        if (!check) throw new IllegalArgumentException("본인이 속하지 않은 채팅방입니다");
       */
//        특정 room에 대한 메시지 조회
        List<ChatMessage> chatMessages = chatMessageRepository.findByChatRoomOrderByCreateTime(chatRoom);
        List<ChatMessageDto> dtos = new ArrayList<>();
        for(ChatMessage c : chatMessages) {
            ChatMessageDto chatMessageDto = ChatMessageDto.builder()
                    .content(c.getContent())
                    .sender(c.getUser().getName())
                    .build();
            dtos.add(chatMessageDto);
        }
        return dtos;
    }

    public List<MyChatRoomListResDto> getMyChatRoom() {
        User user = userRepository.findBySocialId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 사용자가 참여한 채팅방 목록을 가져옴
        List<ChatParticipant> participants = chatParticipantRepository.findAllByUser(user);

        return participants.stream()
                .map(participant -> participant.getChatRoom())
                .filter(chatRoom -> !"CLOSED".equals(chatRoom.getRoom_status()))  // "CLOSED" 상태인 채팅방 제외
                .map(chatRoom -> MyChatRoomListResDto.from(chatRoom, chatRoom.getCreator().getId().equals(user.getId())))
                .collect(Collectors.toList());
    }


    public void closeChatRoom(Long roomId, int score) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("채팅방을 찾을 수 없습니다."));

        // 현재 요청한 사용자
        User currentUser = userRepository.findBySocialId(
                SecurityContextHolder.getContext().getAuthentication().getName()
        ).orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 방장 확인 (방장만 종료 가능)
        if (!chatRoom.getCreator().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("방 생성자만 종료할 수 있습니다.");
        }

        //  멘토 찾기
        ChatParticipant mentorParticipant = chatParticipantRepository
                .findByChatRoomIdAndChatRole(roomId, ChatRole.MENTOR)
                .orElseThrow(() -> new EntityNotFoundException("멘토를 찾을 수 없습니다."));

        User mentor = mentorParticipant.getUser();
        mentor.addPoint(score);

        // 저장
        userRepository.save(mentor);
        chatRoomRepository.deleteById(roomId);
    }


    public void leaveChatRoom(Long roomId) {
        // 채팅방 찾기
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("채팅방을 찾을 수 없습니다."));

        // 현재 사용자 찾기
        User user = userRepository.findBySocialId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 해당 채팅방에 사용자가 참여하고 있는지 확인
        ChatParticipant participant = chatParticipantRepository.findByChatRoomAndUser(chatRoom, user)
                .orElseThrow(() -> new IllegalStateException("이 채팅방에 참여하고 있지 않습니다."));

        // 나가기
        chatParticipantRepository.delete(participant);
        chatRoom.decreaseCurrentMemberCount();  // 현재 참여자 수 감소

        // 만약 채팅방에 참여자가 없으면 채팅방을 종료 상태로 변경
        if (chatRoom.getCurrentMemberCount() == 0) {
            chatRoom.closeRoom();
            chatRoomRepository.deleteById(roomId);
        } else {
            chatRoomRepository.save(chatRoom); // 참여자 수를 업데이트한 후 저장
        }
    }

}
