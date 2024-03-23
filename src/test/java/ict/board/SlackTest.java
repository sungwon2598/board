package ict.board;

import ict.board.service.SlackService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;

@SpringBootTest
class SlackServiceTest {
    @Autowired
    SlackService slackService;

    @Test
    @DisplayName("Slack 메시지 전송")
    void sendMessage() {
        String title = "Slack 메시지 전송 테스트";
        HashMap<String, String> data = new HashMap<>();
        data.put("테스트1", "테스트1 내용");
        data.put("테스트2", "테스트2 내용");

        slackService.sendMessage(title, data);
    }
}
