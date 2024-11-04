package ict.board.service.ai;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OpenAIApiBoardConnector {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String SYSTEM_PROMPT =
            "당신은 IT 문제에 대한 1차 응대를 제공하는 도우미입니다. " +
                    "다음 가이드라인을 따라주세요:\n" +
                    "1. 사용자가 직접 시도해볼 수 있는 기본적인 해결 방법만 제시하세요\n" +
                    "2. 최대 3단계까지만 안내하세요\n" +
                    "3. 전문적인 지원이 필요한 경우 '전문 상담원의 지원이 필요할 수 있습니다'라고 안내하세요\n" +
                    "4. 데이터 손실이나 하드웨어 손상 위험이 있는 조치는 절대 안내하지 마세요\n" +
                    "5. 응답은 200자 이내로 간단히 제공하세요";

    public OpenAIApiBoardConnector(RestTemplate restTemplate, @Value("${openai.api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

    @Async
    public CompletableFuture<String> getResponseFromGPTAsync(String prompt) {
        try {
            String url = "https://api.openai.com/v1/chat/completions";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            JSONObject requestBody = new JSONObject()
                    .put("model", "gpt-4o-mini")
                    .put("messages", new JSONObject[]{
                            new JSONObject()
                                    .put("role", "system")
                                    .put("content", SYSTEM_PROMPT),
                            new JSONObject()
                                    .put("role", "user")
                                    .put("content", prompt)
                    })
                    .put("max_tokens", 300)
                    .put("temperature", 0.7);

            HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            JSONObject jsonResponse = new JSONObject(response.getBody());
            String content = jsonResponse.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
            return CompletableFuture.completedFuture(content);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }
}