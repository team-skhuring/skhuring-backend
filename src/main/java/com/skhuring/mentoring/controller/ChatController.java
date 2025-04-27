package com.skhuring.mentoring.controller;

import com.skhuring.mentoring.dto.ChatMessageDto;
import com.skhuring.mentoring.dto.ChatRoomListResDto;
import com.skhuring.mentoring.dto.CreateChatRoomReqDto;
import com.skhuring.mentoring.dto.JoinChatRoomReqDto;
import com.skhuring.mentoring.service.ChatService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/chat")
@AllArgsConstructor
@Slf4j
public class ChatController {
    private final ChatService chatService;
//    채팅방 개설
    @PostMapping("/room")
    public ResponseEntity<?> createChatRoom(@RequestBody CreateChatRoomReqDto request) {
        log.info(SecurityContextHolder.getContext().getAuthentication().getName());
        try {
            chatService.createRoom(request);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
//    채팅목록조회
    @GetMapping("/rooms/list")
    public ResponseEntity<?> getChatRoomsList() {
        List<ChatRoomListResDto> chatRooms =  chatService.getChatRoomsList();
        return ResponseEntity.ok().body(chatRooms);
    }

//    채팅방 참여
    @PostMapping("/room/join")
    public ResponseEntity<?> enterChatRoom(@RequestBody JoinChatRoomReqDto requestDto) {
        String role = requestDto.getRole();
        chatService.addParticipantToChatRoom(requestDto);
        return ResponseEntity.ok().build();
    }

//    이전 메시지 조회
    @GetMapping("/history/{roomId}")
    public ResponseEntity<?> getChatRoomHistory(@PathVariable String roomId) {
        List<ChatMessageDto> chatMessageDtos = chatService.getChatHistory(roomId);
        return new ResponseEntity<>(chatMessageDtos, HttpStatus.OK);
    }


}
