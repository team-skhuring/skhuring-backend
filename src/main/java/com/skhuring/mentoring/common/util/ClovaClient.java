package com.skhuring.mentoring.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ClovaClient {

    @Value("${clova.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String callClovaChat(String userMessage) {
        String url = "https://clovastudio.stream.ntruss.com/testapp/v1/chat-completions/HCX-DASH-001";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // AI 답변 방식 설정
        Map<String, Object> systemMessage = Map.of(
                "role", "system",
                "content", "너는 사용자의 채팅 내용과 궁금한 키워드에 관한 내용을 받게 될거야. 키워드를 설명해주면 돼. 내용만 짧게 이야기해줘."
        );

        // 사용자 질문 전달
        Map<String, Object> userMsg = Map.of(
                "role", "user",
                "content", userMessage
        );

        // 답변 설정
        Map<String, Object> body = new HashMap<>();
        body.put("messages", List.of(systemMessage, userMsg));
        body.put("topP", 0.8);
        body.put("topK", 0);
        body.put("temperature", 0.5);
        body.put("maxTokens", 101);
        body.put("repeatPenalty", 1.1);
        body.put("includeAiFilters", true);
        body.put("seed", 0);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        Map result = (Map) response.getBody().get("result");
        Map message = (Map) result.get("message");
        return (String) message.get("content");
    }
}
