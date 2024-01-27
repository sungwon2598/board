package ict.board.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import java.util.Collections;
import org.json.JSONObject;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AiClient {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private static final Logger logger = LoggerFactory.getLogger(AiClient.class);

    public AiClient(RestTemplate restTemplate, @Value("${openai.api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

    @Async
    public CompletableFuture<String> getResponseFromGPTAsync(String prompt) {
        logger.info("getResponseFromGPTAsync called with prompt: {}", prompt);
        try {
            String url = "https://api.openai.com/v1/chat/completions";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            String requestBody = "{\"model\": \"gpt-3.5-turbo\", \"messages\": [{\"role\": \"system\", \"content\": \"This system is used for responding to questions about computer hardware and software issues.\"}, {\"role\": \"user\", \"content\": \"" + prompt + "\"}]}";
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);


            logger.info("Sending request to GPT-3 API");
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            logger.info("Response received from GPT-3 API");

            JSONObject jsonResponse = new JSONObject(response.getBody());
            String content = jsonResponse.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");

            return CompletableFuture.completedFuture(content);
        } catch (Exception e) {
            logger.error("Error during GPT-3 API call", e);
            return CompletableFuture.failedFuture(e);
        }
    }
}


