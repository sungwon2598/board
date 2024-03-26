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
public class OpenAIApiConnector {

    private final RestTemplate restTemplate;
    private final String apiKey;

    public OpenAIApiConnector(RestTemplate restTemplate, @Value("${openai.api.key}") String apiKey) {
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

            String requestBody =
                    "{\"model\": \"gpt-3.5-turbo\", \"messages\": [{\"role\": \"system\", \"content\": "
                            + "\"This system is used for responding to questions about computer hardware and software issues.\"}"
                            + ", {\"role\": \"user\", \"content\": \""
                            + prompt + "\"}]}";
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            JSONObject jsonResponse = new JSONObject(response.getBody());
            String content = jsonResponse.getJSONArray("choices").getJSONObject(0).getJSONObject("message")
                    .getString("content");
            return CompletableFuture.completedFuture(content);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }
}