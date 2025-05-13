package com.skhuring.mentoring.controller;

import com.skhuring.mentoring.dto.MemoReqDto;
import com.skhuring.mentoring.dto.MemoResDto;
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
        try {
            MemoResDto memo = memoService.getMemoByChatRoomId(chatRoomId);
            return ResponseEntity.ok(memo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생");
        }
    }

    /* 유저 ID 로 메모 조회 */
    @GetMapping("/user")
    public ResponseEntity<?> user(@RequestParam("userId") Long userId) {
        try {
            List<MemoResDto> memoList = memoService.getMemoByUserId(userId);
            return ResponseEntity.ok(memoList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생");
        }
    }

    /* 입장한 채팅방 ID 에 따른 메모 생성 */
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody MemoReqDto dto) {
        try {
            log.info("memo create {}", dto);
            memoService.createMemo(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body("메모 생성 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생");
        }
    }

    /* 메모 수정 */
    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody MemoReqDto dto) {
        try {
            memoService.updateMemo(dto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생");
        }
    }

    /* 메모 삭제 */
    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam("memoId") Long memoId) {
        try {
            memoService.deleteMemo(memoId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생");
        }
    }

}
