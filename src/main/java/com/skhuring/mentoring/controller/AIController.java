package com.skhuring.mentoring.controller;

import com.skhuring.mentoring.dto.AIReqDto;
import com.skhuring.mentoring.dto.AIResDto;
import com.skhuring.mentoring.service.AIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
//@RequestMapping("/ai")
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private final AIService aiService;

    /* 사용자의 질문을 AI 에게 답변 요청 보내기 */
    @PostMapping("/qa")
    public ResponseEntity<AIResDto> answerQuestion(@RequestBody AIReqDto AIReqDto) {
         // 사용자의 질문 전달
        return ResponseEntity.ok(aiService.answerQuestion(AIReqDto.getSentence(), AIReqDto.getQuestion()));
    }
}
