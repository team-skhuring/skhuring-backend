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
    public AIResDto answerQuestion(String sentence, String question) {
        String prompt = sentence + "\n해당 문장을 바탕으로 다음 질문에 짧게 답변. " + question;
        String result = clovaClient.callClovaChat(prompt);
        return AIResDto.builder()
                .answer(result)
                .build();
    }
}
