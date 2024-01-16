package ict.board.service;


import ict.board.domain.board.Board;
import ict.board.domain.board.BoardStatus;
import ict.board.domain.member.Address;
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

    public List<Board> findAllBoards() {
        return boardRepostiory.findAll();
    }

    public Board findOneBoard(Long id) {
        return boardRepostiory.findBoardById(id);
    }
}
