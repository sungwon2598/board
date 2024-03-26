package ict.board.service;


import ict.board.domain.board.Board;
import ict.board.domain.board.BoardStatus;
import ict.board.domain.member.Member;
import ict.board.repsoitory.BoardRepostiory;
import ict.board.repsoitory.MemberRepository;
import ict.board.service.ai.AIResponseHandler;
import java.io.IOException;
import java.util.List;
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
    private final AIResponseHandler AIResponseHandler;
    private final SlackService slackService;

    @Transactional
    public void save(Board board, String loginMemberEmail) throws IOException, InterruptedException {

        board.addMember(memberRepository.findMemberByEmail(loginMemberEmail).orElse(null));
        boardRepostiory.save(board);

        String ask = board.getContent();

        slackService.sendFromBoard(board);
        AIResponseHandler.answerGpt(board, ask);
    }

    @Transactional
    public void delete(Long id) {
        boardRepostiory.deleteById(id);
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

    public Page<Board> findAllBoards(Pageable pageable) {
        return boardRepostiory.findAllWithMember(pageable);
    }

    public Board findOneBoard(Long id) {
        return boardRepostiory.findWithMemberById(id);
    }

    public List<Board> findBoardsbyMember(Member member) {
        return boardRepostiory.findByMember(member);
    }

}
