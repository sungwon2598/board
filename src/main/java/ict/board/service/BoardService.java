package ict.board.service;


import ict.board.domain.board.Board;
import ict.board.domain.board.BoardStatus;
import ict.board.domain.member.Member;
import ict.board.domain.reply.Reply;
import ict.board.repsoitory.BoardRepostiory;
import ict.board.repsoitory.MemberRepository;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {

    private final BoardRepostiory boardRepostiory;
    private final MemberRepository memberRepository;
    private final AiClient aiClient;
    private final ReplyService replyService;
    private final SlackService slackService;

    @Transactional
    public void save(Board board, String loginMemberEmail) throws IOException, InterruptedException {

        board.addMember(memberRepository.findMemberByEmail(loginMemberEmail).orElse(null));
        boardRepostiory.save(board);

        String ask = board.getContent();

        //slackMessaging(board);

        answerGpt(board, ask);
    }

    private void answerGpt(Board board, String ask) {
        Member member = memberRepository.findById(2L).orElse(null);
        Reply simpleReply = new Reply("AI-ICT가 답변을 작성중입니다 조금만 기다려주세요", board, member);
        Long replyId = replyService.save(simpleReply);
        CompletableFuture<String> chatFuture = aiClient.getResponseFromGPTAsync(ask);

        chatFuture.thenAccept(chat -> {
            replyService.updateReply(replyId, chat);
        });
    }

    private void slackMessaging(Board board) {
        String title = "작성자 : " + board.getMember().getName() + "  소속 : " + board.getMember().getTeam();
        HashMap<String, String> data = new HashMap<>();
        data.put(board.getTitle(), board.getContent());

        slackService.sendMessage(title, data);
    }

    @Transactional
    public void delete(Long id) {
        boardRepostiory.deleteById(id);
    }


    public Page<Board> findAllBoards(Pageable pageable) {
        return boardRepostiory.findAllWithMember(pageable);
    }

    public Board findOneBoard(Long id) {
        return boardRepostiory.findWithMemberById(id);
    }

    public boolean checkCredentials(Long id, String userId, String password) {
        Board board = boardRepostiory.findWithMemberById(id);
        Member member = board.getMember();
        return member.getEmail().equals(userId) && member.getPassword().equals(password);
    }

    public List<Board> findBoardsbyMember(Member member) {
        return boardRepostiory.findByMember(member);
    }

    @Transactional
    public void changeBoardStatus(Long id, BoardStatus boardStatus) {
        Board board = boardRepostiory.findById(id).orElse(null);
        board.changeStatus(boardStatus);
    }

    @Transactional
    public void update(Long id, String newTitle, String newContent) {
        Board board = boardRepostiory.findById(id).orElse(null);
        board.changeTitle(newTitle);
        board.chageContent(newContent);
    }
}
