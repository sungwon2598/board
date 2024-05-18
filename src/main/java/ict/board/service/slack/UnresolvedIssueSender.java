package ict.board.service.slack;

import ict.board.domain.board.Board;
import ict.board.domain.board.BoardStatus;
import ict.board.repository.BoardRepository;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UnresolvedIssueSender {

    private final BoardRepository boardRepository;
    private final SlackMessageSender slackMessageSender;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM월 dd일 HH시 mm분");

    @Scheduled(cron = "0 23 16 * * *")
    @Transactional
    public void logUncheckedBoards() {
        List<Board> uncheckedBoards = boardRepository.findByBoardStatus(BoardStatus.UNCHECKED);

        if (!uncheckedBoards.isEmpty()) {
            sendFromBoards(uncheckedBoards);
        }
    }

    @Transactional
    public void sendFromBoards(List<Board> boards) {
        String title = "미조치 민원 목록";
        HashMap<String, String> data = new HashMap<>();

        boards.forEach(board -> {
            String boardDetails = "요청자: " + board.getRequester() + ", 위치: " + board.getRequesterLocation()
                    + " 요청시간: " + board.getCreatedAt().format(formatter);
            data.put(board.getTitle(), boardDetails);
        });

        slackMessageSender.sendMessage(slackMessageSender.getSlackWebhookUrlNon(), title, data);
    }
}
