package com.skhuring.mentoring.controller;

import com.skhuring.mentoring.dto.*;
import com.skhuring.mentoring.service.ChatService;
import com.skhuring.mentoring.service.S3Uploader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
//@RequestMapping("/chat")
@RequestMapping("/api/chat")
@AllArgsConstructor
@Slf4j
public class ChatController {
    private final ChatService chatService;

    private final S3Uploader s3Uploader;
//    채팅방 개설
    @PostMapping("/room")
    public ResponseEntity<?> createChatRoom(@RequestBody CreateChatRoomReqDto request) {
        log.info(SecurityContextHolder.getContext().getAuthentication().getName());
        try {
            Long roomId = chatService.createRoom(request);
            return ResponseEntity.ok(roomId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
//    채팅목록조회
    @GetMapping("/rooms/list")
    public ResponseEntity<?> getChatRoomsList() {
        List<ChatRoomListResDto> chatRooms =  chatService.getChatRoomsList();
        return ResponseEntity.ok().body(chatRooms);
    }

//    내가 참여한 채팅방 목록 조회
@GetMapping("/rooms/me")
public ResponseEntity<?> getChatRoomsMe() {
        List<MyChatRoomListResDto> myChatRooms = chatService.getMyChatRoom();
        return ResponseEntity.ok().body(myChatRooms);
}

//    채팅방 참여
    @PostMapping("/room/join")
    public ResponseEntity<?> enterChatRoom(@RequestBody JoinChatRoomReqDto requestDto) {
        String role = requestDto.getRole();
        chatService.addParticipantToChatRoom(requestDto);
        return ResponseEntity.ok().build();
    }

//    이전 메시지 조회
    @GetMapping("/history/{roomId}")
    public ResponseEntity<?> getChatRoomHistory(@PathVariable String roomId) {
        List<ChatMessageDto> chatMessageDtos = chatService.getChatHistory(roomId);
        return new ResponseEntity<>(chatMessageDtos, HttpStatus.OK);
    }
//    채팅방 종료 - 방장
    @PostMapping("/close/{roomId}")
    public ResponseEntity<?> closeChatRoom(
            @PathVariable String roomId,
            @RequestBody Map<String, Integer> body) {
        Integer score = body.get("score");
        chatService.closeChatRoom(Long.valueOf(roomId), score);
        return ResponseEntity.ok().body("채팅방이 종료되었습니다.");
    }
//     채팅방 나가기 - 일반 사용자
    @PostMapping("/leave/{roomId}")
    public ResponseEntity<String> leaveChatRoom(@PathVariable String roomId) {
        try {
            chatService.leaveChatRoom(Long.valueOf(roomId));
            return ResponseEntity.ok("채팅방을 성공적으로 나갔습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
    @PostMapping("/upload/{roomId}")
    public ResponseEntity<Map<String, String>> upload(@RequestParam("file") MultipartFile file) throws IOException {
        String url = s3Uploader.upload(file);
        Map<String, String> response = new HashMap<>();
        response.put("messageType", "IMAGE");
        response.put("url", url);
        return ResponseEntity.ok(response);
    }
//    메시지 읽음 처리 api
    @PostMapping("/chatrooms/{roomId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long roomId, @RequestParam Long userId) {
        chatService.markMessagesAsRead(roomId, userId);
        return ResponseEntity.ok().build();
    }
//   채팅방 목록 조회 시 각방에 있는 안읽은 메시지 개수 함께 반환
    @GetMapping("/with-unread")
    public ResponseEntity<List<ChatRoomWithUnreadDto>> getChatRoomsWithUnread(@RequestParam Long userId) {
        List<ChatRoomWithUnreadDto> chatRooms = chatService.getChatRoomsWithUnreadCounts(userId);
        return ResponseEntity.ok(chatRooms);
    }


}
