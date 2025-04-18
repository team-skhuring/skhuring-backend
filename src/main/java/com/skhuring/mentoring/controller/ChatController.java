package com.skhuring.mentoring.controller;

import com.skhuring.mentoring.dto.CreateChatRoomReqDto;
import com.skhuring.mentoring.service.ChatService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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

}
