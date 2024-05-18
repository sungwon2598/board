package ict.board.service.slack;

import com.slack.api.Slack;
import com.slack.api.model.Attachment;
import com.slack.api.model.Field;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.slack.api.webhook.WebhookPayloads.payload;

@Component
@Getter
public class SlackMessageSender {

    private final Slack slackClient = Slack.getInstance();

    @Value("${webhook.slack.url-non}")
    private String slackWebhookUrlNon;

    @Value("${webhook.slack.url-board}")
    private String slackWebhookUrlBoard;

    @Value("${webhook.slack.url-reservation}")
    private String slackWebhookUrlReservation;

    public void sendMessage(String webhookUrl, String title, HashMap<String, String> data) {
        try {
            slackClient.send(webhookUrl, payload(p -> p
                    .text(title)
                    .attachments(List.of(
                            Attachment.builder()
                                    .fields(
                                            data.keySet().stream().map(key -> generateSlackField(key, data.get(key)))
                                                    .collect(Collectors.toList())
                                    ).build())))
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Field generateSlackField(String title, String value) {
        return Field.builder()
                .title(title)
                .value(value)
                .valueShortEnough(false)
                .build();
    }

}

