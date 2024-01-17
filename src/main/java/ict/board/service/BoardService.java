package ict.board.service;


import ict.board.domain.board.Board;
import ict.board.domain.board.BoardStatus;
import ict.board.domain.member.Member;
import ict.board.domain.reply.Reply;
import ict.board.repsoitory.BoardRepostiory;
import ict.board.repsoitory.MemberRepository;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepostiory boardRepostiory;
    private final MemberRepository memberRepository;
    private final LoginService loginService;
    private final AiClient aiClient;
    private final ReplyService replyService;

    @Transactional
    public Long save(Board board, String email, String password) throws IOException, InterruptedException {

        loginService.login(email, password);
        board.addMember(memberRepository.findMemberByEmail(email));
        board.setBoardStatus(BoardStatus.UNCHECKED);
        boardRepostiory.save(board);

        Reply reply = new Reply();
        reply.addBoard(board);
        String ask = board.getContent();

        String chat = aiClient.getResponseFromGPT(ask); // ChatGPT 응답 받기
        reply.setContent(chat);

        Member member = memberRepository.findOne(702L);
        reply.addMember(member);
        replyService.save(reply);

        return board.getId();
    }

    @Transactional
    public void update(Long id, String newTitle, String newContent) {
        boardRepostiory.updateBoard(id, newTitle, newContent);
    }

    public List<Board> findAllBoards() {
        return boardRepostiory.findAll();
    }

    public Board findOneBoard(Long id) {
        return boardRepostiory.findBoardById(id);
    }

    public boolean checkCredentials(Long id, String userId, String password) {
        Board board = boardRepostiory.findOne(id);
        Member member = board.getMember();
        if (!member.getEmail().equals(userId) || !member.getPassword().equals(password)) {
            return false;
        }
        return true;
    }

    @Transactional
    public void delete(List<Long> ids, Long id) {
        replyService.deleteAll(ids);
        boardRepostiory.deleteOne(id);
    }

    @Transactional
    public void updateStatus(Long id, BoardStatus status) {
        boardRepostiory.updateStatus(id, status);
    }

}
