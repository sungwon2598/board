package ict.board.service;


import ict.board.domain.board.Board;
import ict.board.repsoitory.BoardRepostiory;
import ict.board.repsoitory.MemberRepository;
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

    @Transactional
    public Long save(Board board, String email, String password) {

        loginService.login(email, password);
        board.addMember(memberRepository.findMemberByEmail(email));
        boardRepostiory.save(board);
        return board.getId();
    }

    public List<Board> findAllBoards() {
        return boardRepostiory.findAll();
    }

    public Board findOneBoard(Long id) {
        return boardRepostiory.findBoardById(id);
    }
}
