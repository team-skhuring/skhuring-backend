package com.skhuring.mentoring.service;

import com.skhuring.mentoring.common.util.ClovaClient;
import com.skhuring.mentoring.dto.AIResDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class AIService {

    private final ClovaClient clovaClient;

    /* 사용자의 질문을 AI 에게 답변 요청 보내기 */
    public AIResDto answerQuestion(String question) {
        String result = clovaClient.callClovaChat(question);
        return AIResDto.builder()
                .answer(result)
                .build();
    }
}
