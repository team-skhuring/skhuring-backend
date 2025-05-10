package com.skhuring.mentoring.controller;

import com.skhuring.mentoring.dto.MemoDto;
import com.skhuring.mentoring.service.MemoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/memo")
@RequiredArgsConstructor
public class MemoController {
    private final MemoService memoService;

    /* 채팅방 ID 로 메모 조회 */
    @GetMapping("/chatroom")
    public ResponseEntity<?> chatroom(@RequestParam("chatRoomId") Long chatRoomId) {
        MemoDto memo = memoService.getMemoByChatRoomId(chatRoomId);
        return ResponseEntity.ok(memo);
    }

    /* 유저 ID 로 메모 조회 */
    @GetMapping("/user")
    public ResponseEntity<?> user(@RequestParam("userId") Long userId) {
        List<MemoDto> memoList = memoService.getMemoByUserId(userId);
        return ResponseEntity.ok(memoList);
    }

    /* 메모 수정 */
    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestParam("memoId") Long memoId,
                                  @RequestBody MemoDto dto) {
        try {
            memoService.updateMemo(memoId, dto);
            return ResponseEntity.ok().build(); // 성공 시 200 OK
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생");
        }
    }

}
