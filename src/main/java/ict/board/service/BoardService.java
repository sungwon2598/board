package ict.board.service;


import ict.board.domain.board.Board;
import ict.board.domain.board.BoardStatus;
import ict.board.domain.member.Member;
import ict.board.domain.reply.Reply;
import ict.board.repsoitory.BoardRepostiory;
import ict.board.repsoitory.MemberRepository;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepostiory boardRepostiory;
    private final MemberRepository memberRepository;
    private final LoginService loginService;
    private final AiClient aiClient;
    private final ReplyService replyService;
    private static final Logger logger = LoggerFactory.getLogger(BoardService.class);

    @Transactional
    public Long save(Board board, String email, String password) throws IOException, InterruptedException {
        loginService.login(email, password);
        board.addMember(memberRepository.findMemberByEmail(email));
        board.setBoardStatus(BoardStatus.UNCHECKED);
        boardRepostiory.save(board);

        String ask = board.getContent();
        Reply simpleReply = new Reply();
        simpleReply.setContent("AI-ICT가 답변을 작성중입니다 조금만 기다려주세요");
        Member member = memberRepository.findOne(903L);
        simpleReply.addMember(member);
        simpleReply.setBoard(board);
        Long replyId = replyService.save(simpleReply);
        CompletableFuture<String> chatFuture = aiClient.getResponseFromGPTAsync(ask);

        chatFuture.thenAccept(chat -> {
            replyService.updateReply(replyId, chat);
        });

        return board.getId();
    }

    @Transactional
    public void update(Long id, String newTitle, String newContent) {
        boardRepostiory.updateBoard(id, newTitle, newContent);
    }

    @Transactional
    public void delete(Long id) {
        boardRepostiory.deleteOne(id);
    }

    @Transactional
    public void updateStatus(Long id, BoardStatus status) {
        boardRepostiory.updateStatus(id, status);
    }

    public List<Board> findAllBoards() {
        return boardRepostiory.findAll();
    }

    public Board findOneBoard(Long id) {
        return boardRepostiory.findOne(id);
    }

    public boolean checkCredentials(Long id, String userId, String password) {
        Board board = boardRepostiory.findOne(id);
        Member member = board.getMember();
        return member.getEmail().equals(userId) && member.getPassword().equals(password);
    }

    public List<Board> findBoardsbyMember(Member member) {
        return boardRepostiory.findBoardsMyMember(member);
    }

}
