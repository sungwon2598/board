package ict.board.service.slack;

import ict.board.domain.board.Board;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NewBoardSender {

    private final SlackMessageSender slackMessageSender;

    public NewBoardSender(SlackMessageSender slackMessageSender) {
        this.slackMessageSender = slackMessageSender;
    }

    @Async
    public CompletableFuture<Void> sendFromBoard(Board board) {
        String title = "작성자 : " + board.getMember().getName() + "  소속 : " + board.getMember().getTeam()
                + " 위치 : " + board.getRequesterLocation() + " 요청자 : " + board.getRequester();
        HashMap<String, String> data = new HashMap<>();
        data.put(board.getTitle(), board.getContent());

        return CompletableFuture.runAsync(() -> {
            slackMessageSender.sendMessage(slackMessageSender.getSlackWebhookUrlBoard(), title, data);
        });
    }
}
