package com.skhuring.mentoring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomListResDto {
    private Long roomId;
    private String title;
    private String category;
    private boolean mentor_status; // null 허용
    private String creatorName;
    private int currentMemberCount;
    private boolean isFull; // ✅ 정원 초과 여부 추가
}
