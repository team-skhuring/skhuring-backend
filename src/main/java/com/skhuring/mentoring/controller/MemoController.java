package com.skhuring.mentoring.controller;

import com.skhuring.mentoring.dto.MemoResDto;
import com.skhuring.mentoring.service.MemoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/memo")
@RequiredArgsConstructor
public class MemoController {
    private final MemoService memoService;

    /* 채팅방 ID 로 메모 조회 */
    @GetMapping("/chatroom")
    public ResponseEntity<?> chatroom(@RequestParam(value = "chatRoomId") Long chatRoomId) {
        MemoResDto memo = memoService.getMemoByChatRoomId(chatRoomId);
        return ResponseEntity.ok(memo);
    }

    /* 유저 ID 로 메모 조회 */
    @GetMapping("/user")
    public ResponseEntity<?> user(@RequestParam(value = "userId") Long userId) {
        List<MemoResDto> memoList = memoService.getMemoByUserId(userId);
        return ResponseEntity.ok(memoList);
    }

}
