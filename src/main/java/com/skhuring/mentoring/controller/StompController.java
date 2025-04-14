package com.skhuring.mentoring.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class StompController {
    @MessageMapping("/{roomId}")
    @SendTo("/topic/{roomId}")
    public String sendMessage(@DestinationVariable String roomId, String message) {
        log.info("sendMessage: {}", message);
        return message;
    }
}
