package ict.board.service.slack;

import ict.board.domain.board.Board;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Service
public class NewBoardSender {

    private final SlackMessageSender slackMessageSender;

    public NewBoardSender(SlackMessageSender slackMessageSender) {
        this.slackMessageSender = slackMessageSender;
    }

    @Transactional
    public void sendFromBoard(Board board) {
        String title = "작성자 : " + board.getMember().getName() + "  소속 : " + board.getMember().getTeam();
        HashMap<String, String> data = new HashMap<>();
        data.put(board.getTitle(), board.getContent());

        slackMessageSender.sendMessage(slackMessageSender.getSlackWebhookUrlBoard(), title, data);
    }
}
