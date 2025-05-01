package com.skhuring.mentoring.dto;

import com.skhuring.mentoring.domain.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyChatRoomListResDto {
    private Long roomId;
    private String title;
    public static MyChatRoomListResDto from(ChatRoom chatRoom) {
        return MyChatRoomListResDto.builder()
                .roomId(chatRoom.getId())  // roomId를 추가
                .title(chatRoom.getTitle())
                .build();
    }
}
