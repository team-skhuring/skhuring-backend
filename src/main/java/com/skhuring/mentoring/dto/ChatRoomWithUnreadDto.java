package com.skhuring.mentoring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatRoomWithUnreadDto {
    private Long roomId;
    private String title;
    private String recentMessage;
    private Long unreadCount;
}
