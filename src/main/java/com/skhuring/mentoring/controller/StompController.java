package com.skhuring.mentoring.controller;

import com.skhuring.mentoring.dto.ChatMessageReqDto;
import com.skhuring.mentoring.service.ChatService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
@AllArgsConstructor
public class StompController {
    private final SimpMessageSendingOperations messageTemplate;

    private final ChatService chatService;

    @MessageMapping("/{roomId}")
  //  @SendTo("/topic/{roomId}")
    public void sendMessage(@DestinationVariable String roomId, ChatMessageReqDto chatMessageReqDto) {
        log.info("sendMessage: {}", chatMessageReqDto.toString());
        chatService.saveMessage(Long.valueOf(roomId), chatMessageReqDto);
        messageTemplate.convertAndSend("/topic/"+roomId, chatMessageReqDto);
    }
}
