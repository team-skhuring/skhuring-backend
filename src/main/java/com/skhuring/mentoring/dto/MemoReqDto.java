package com.skhuring.mentoring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemoReqDto {
    private Long id;
    private Long chatRoomId;
    private String title;
    private String content;
    private String socialId;
}
