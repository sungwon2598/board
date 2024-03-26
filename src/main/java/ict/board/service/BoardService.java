package ict.board.service;


import ict.board.domain.board.Board;
import ict.board.domain.board.BoardStatus;
import ict.board.domain.member.Member;
import ict.board.repsoitory.BoardRepository;
import ict.board.repsoitory.MemberRepository;
import ict.board.service.ai.AIResponseHandler;
import jakarta.persistence.EntityNotFoundException;
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

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final AIResponseHandler AIResponseHandler;
    private final SlackService slackService;

    @Transactional
    public void save(Board board, String loginMemberEmail) throws IOException, InterruptedException {

        board.addMember(memberRepository.findMemberByEmail(loginMemberEmail).orElse(null));
        boardRepository.save(board);

        String ask = board.getContent();

        slackService.sendFromBoard(board);
        AIResponseHandler.answerGpt(board, ask);
    }

    @Transactional
    public void delete(Long id) {
        boardRepository.deleteById(id);
    }

    @Transactional
    public boolean changeBoardStatus(Long id, BoardStatus boardStatus, String adminPassword) {
        if (!isAdminPasswordValid(adminPassword)) {
            return false;
        }

        Board board = boardRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Board not found"));
        board.changeStatus(boardStatus);
        return true;
    }

    private boolean isAdminPasswordValid(String adminPassword) {
        return "024907345".equals(adminPassword);
    }

    @Transactional
    public void update(Long id, String newTitle, String newContent) {
        Board board = boardRepository.findById(id).orElse(null);
        board.changeTitle(newTitle);
        board.chageContent(newContent);
    }

    public Page<Board> findAllBoards(Pageable pageable) {
        return boardRepository.findAllWithMember(pageable);
    }

    public Board findOneBoard(Long id) {
        return boardRepository.findWithMemberById(id);
    }

    public List<Board> findBoardsbyMember(Member member) {
        return boardRepository.findByMember(member);
    }

}
