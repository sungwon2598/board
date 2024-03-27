package ict.board.service.ai;

import ict.board.domain.board.Board;
import ict.board.domain.member.Member;
import ict.board.domain.reply.Reply;
import ict.board.repsoitory.MemberRepository;
import ict.board.service.MemberService;
import ict.board.service.ReplyService;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AIResponseHandler {

    private final MemberRepository memberRepository;
    private final ReplyService replyService;
    private final OpenAIApiConnector aiCliente;

    public void answerGpt(Board board, String ask) {

        Member aiIct = memberRepository.findById(2L).orElse(null);
        Reply simpleReply = new Reply("AI-ICT가 답변을 작성중입니다 조금만 기다려주세요", board, aiIct);
        Long replyId = replyService.save(simpleReply);
        CompletableFuture<String> chatFuture = aiCliente.getResponseFromGPTAsync(ask);

        chatFuture.thenAccept(chat -> {
            replyService.updateReply(replyId, chat);
        });
    }
}