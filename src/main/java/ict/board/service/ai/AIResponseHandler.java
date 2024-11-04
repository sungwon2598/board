package ict.board.service.ai;

import ict.board.domain.board.Board;
import ict.board.domain.member.Member;
import ict.board.domain.reply.Reply;
import ict.board.repository.IctStaffMemberRepository;
import ict.board.repository.MemberRepository;
import ict.board.service.board.ReplyService;
import ict.board.exception.AIProcessingException;
import ict.board.exception.MemberNotFoundException;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AIResponseHandler {

    private static final String PROCESSING_MESSAGE = "AI-ICT가 답변을 작성중입니다. 조금만 기다려주세요.";
    private static final String ERROR_MESSAGE = "AI 자동답변 기능이 일시적으로 중단되었습니다. 직원이 곧 답변을 달아드리겠습니다.";

    private final IctStaffMemberRepository staffMemberRepository;
    private final MemberRepository memberRepository;
    private final ReplyService replyService;
    private final OpenAIApiBoardConnector aiClient;

    @Value("${ict.ai-ict.email}")
    private String AI_Member_EMAIL;

    @Transactional
    public CompletableFuture<Void> answerGpt(Board board, String question) {
        Member aiMember = findAIMember();
        Reply initialReply = createInitialReply(board, aiMember);
        Long replyId = replyService.save(initialReply);

        return aiClient.getResponseFromGPTAsync(question)
                .thenApply(response -> updateReplyWithResponse(replyId, response))
                .exceptionally(ex -> handleExceptionAndUpdateReply(replyId, ex));
    }

    private Member findAIMember() {
        return (Member) staffMemberRepository.findByEmail(AI_Member_EMAIL)
                .orElseThrow(() -> new MemberNotFoundException("AI 회원을 찾을 수 없습니다."));
    }

    private Reply createInitialReply(Board board, Member aiMember) {
        return new Reply(PROCESSING_MESSAGE, board, aiMember);
    }

    private Void updateReplyWithResponse(Long replyId, String response) {
        replyService.updateReply(replyId, response);
        return null;
    }

    private Void handleExceptionAndUpdateReply(Long replyId, Throwable ex) {
        replyService.updateReply(replyId, ERROR_MESSAGE);
        throw new AIProcessingException("AI 응답 처리 중 오류가 발생했습니다.", ex);
    }
}