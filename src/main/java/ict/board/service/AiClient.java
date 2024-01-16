package ict.board.service;

import java.util.Collections;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AiClient {

    private final RestTemplate restTemplate;
    private final String apiKey;

    // OpenAI API 키를 application.properties에서 가져옵니다.
    public AiClient(RestTemplate restTemplate, @Value("${openai.api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

    public String getResponseFromGPT(String prompt) {
        // OpenAI chat API URL
        String url = "https://api.openai.com/v1/chat/completions";

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        prompt += "200자 이내로 핵심만 컴퓨터를 하나도 모르는 사람도 쉽게 알아듣게 설명해줘";
        // 요청 본문 구성
        String requestBody =
                "{\"model\": \"gpt-3.5-turbo\", \"messages\": [{\"role\": \"system\", \"content\": \"Your prompt here\"}, {\"role\": \"user\", \"content\": \""
                        + prompt + "\"}]}";
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        // API 요청 및 응답 받기
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        // JSON 객체로 응답을 파싱
        JSONObject jsonResponse = new JSONObject(response.getBody());
        // "choices" 배열에서 첫 번째 요소의 "message" 객체의 "content" 필드를 가져옵니다
        String content = jsonResponse.getJSONArray("choices").getJSONObject(0).getJSONObject("message")
                .getString("content");

        // 추출된 내용 반환
        return content;
    }

}

