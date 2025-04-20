package com.skhuring.mentoring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinChatRoomReqDto {
    private String roomId;
    private String role; // "MENTEE" 또는 "MENTOR"
}
