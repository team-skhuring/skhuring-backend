package com.skhuring.mentoring.dto;

import com.skhuring.mentoring.domain.ChatRole;
import com.skhuring.mentoring.domain.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDto {
    private String content;
    private String sender;
    private MessageType messageType;
    private String socialId;
    private ChatRole chatRole;
}
