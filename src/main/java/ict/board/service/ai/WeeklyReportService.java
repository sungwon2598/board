package ict.board.service.ai;

import ict.board.domain.board.Board;
import ict.board.repository.BoardRepository;
import ict.board.service.ai.OpenAIApiConnector;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class WeeklyReportService {

    private final BoardRepository boardRepository;
    private final OpenAIApiConnector openAIApiConnector;

    public WeeklyReportService(BoardRepository boardRepository, OpenAIApiConnector openAIApiConnector) {
        this.boardRepository = boardRepository;
        this.openAIApiConnector = openAIApiConnector;
    }

    @Async
    public CompletableFuture<String> getWeeklyReport() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        List<Board> weeklyBoards = boardRepository.findByCreatedAtAfter(oneWeekAgo);
        String prompt = formatPrompt(weeklyBoards);
        return openAIApiConnector.getResponseFromGptAsync(prompt);
    }

    private String formatPrompt(List<Board> boards) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("다음은 지난 한 주간의 민원 목록입니다:\n");

        for (Board board : boards) {
            promptBuilder.append("- 제목: ").append(board.getTitle()).append("\n")
                    .append("  내용: ").append(board.getContent()).append("\n")
                    .append("  요청자: ").append(board.getRequester()).append("\n")
                    .append("  요청자 위치: ").append(board.getRequesterLocation()).append("\n")
                    .append("  상태: ").append(board.getBoardStatus().getKoreanName()).append("\n\n");
        }

        promptBuilder.append("위의 민원을 요약해주세요.");
        return promptBuilder.toString();
    }
}