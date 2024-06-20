package ict.board.service;


import ict.board.domain.board.Board;
import ict.board.domain.board.BoardStatus;
import ict.board.domain.member.Member;
import ict.board.repository.BoardRepository;
import ict.board.repository.MemberRepository;
import ict.board.service.ai.AIResponseHandler;
import ict.board.service.slack.NewBoardSender;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final NewBoardSender newBoardSender;

    @Transactional
    public void save(Board board, String loginMemberEmail) throws IOException, InterruptedException {

        board.addMember(memberRepository.findMemberByEmail(loginMemberEmail).orElse(null));
        boardRepository.save(board);

        String ask = board.getContent();

        newBoardSender.sendFromBoard(board);
        AIResponseHandler.answerGpt(board, ask);
    }

    @Transactional
    public void delete(Long id) {
        boardRepository.deleteById(id);
    }

    @Transactional
    public boolean changeBoardStatus(Long id, BoardStatus boardStatus) {
        Board board = boardRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Board not found"));
        board.changeStatus(boardStatus);
        return true;
    }

    @Transactional
    public void update(Long id, String newTitle, String newContent, String requester, String requesterLocation) {
        Board board = boardRepository.findById(id).orElse(null);
        board.changeTitle(newTitle);
        board.changeContent(newContent);
        board.changeRequester(requester);
        board.changeRequesterLocation(requesterLocation);
    }

    public Page<Board> findAllBoardsByDate(Pageable pageable, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        return boardRepository.findAllByCreatedAtBetween(startOfDay, endOfDay, pageable);
    }


    public Page<Board> findAllBoards(Pageable pageable) {
        return boardRepository.findAllWithMember(pageable);
    }

    public Board findOneBoardWithMember(Long id) {
        return boardRepository.findWithMemberById(id);
    }

    public List<Board> findBoardsbyMember(Member member) {
        return boardRepository.findByMember(member);
    }

    public Page<Board> findAllBoardsByStatus(Pageable pageable, String status) {
        BoardStatus boardStatus = BoardStatus.valueOf(status);
        return boardRepository.findAllByBoardStatus(pageable, boardStatus);
    }
}