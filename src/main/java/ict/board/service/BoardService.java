package ict.board.service;

import ict.board.config.argumentresolver.LoginMemberArgumentResolver.LoginSessionInfo;
import ict.board.domain.board.Board;
import ict.board.domain.board.BoardStatus;
import ict.board.domain.board.ReservationBoard;
import ict.board.domain.member.Member;
import ict.board.domain.member.Role;
import ict.board.domain.reply.Reply;
import ict.board.dto.BoardForm;
import ict.board.dto.PostDetail;
import ict.board.repository.BoardRepository;
import ict.board.repository.MemberRepository;
import ict.board.service.ai.AIResponseHandler;
import ict.board.service.slack.NewBoardSender;
import ict.board.util.DateUtils;
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
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {

    private final BoardRepository boardRepository;
    private final ReplyService replyService;
    private final MemberRepository memberRepository;
    private final AIResponseHandler AIResponseHandler;
    private final NewBoardSender newBoardSender;
    private final ReservationBoardService reservationBoardService;

    @Transactional
    public void saveBoard(BoardForm form, String imagePath, String loginMemberEmail) throws IOException, InterruptedException {
        Board board = new Board(form.getTitle(), form.getContent(), form.getRequester(), form.getRequesterLocation(), imagePath);
        save(board, loginMemberEmail);
    }

    @Transactional
    public void saveReservationBoard(BoardForm form, String imagePath, String loginMemberEmail) throws IOException, InterruptedException {
        ReservationBoard reservationBoard = new ReservationBoard(
                form.getTitle(),
                form.getContent(),
                form.getRequester(),
                form.getRequesterLocation(),
                LocalDateTime.of(form.getReservationDate(), form.getReservationTime()),
                imagePath
        );
        save(reservationBoard, loginMemberEmail);
    }

    @Transactional
    protected void save(Board board, String loginMemberEmail) throws IOException, InterruptedException {
        board.addMember(memberRepository.findMemberByEmail(loginMemberEmail).orElse(null));
        boardRepository.save(board);

        String ask = board.getContent();

        newBoardSender.sendFromBoard(board);
        AIResponseHandler.answerGpt(board, ask);
    }

    @Transactional
    public boolean deleteBoard(Long id, LoginSessionInfo loginSessionInfo) {
        Board board = boardRepository.findById(id).orElse(null);
        if (board == null || loginSessionInfo == null || !board.getMember().getEmail().equals(loginSessionInfo.getEmail())) {
            return false;
        }
        replyService.deleteRepliesByBoardId(id);
        boardRepository.deleteById(id);
        return true;
    }

    @Transactional
    public boolean changeBoardStatus(Long id, BoardStatus boardStatus) {
        Board board = boardRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Board not found"));
        board.changeStatus(boardStatus);
        return true;
    }

    @Transactional
    public void updateBoard(Long id, String newTitle, String newContent, String requester, String requesterLocation) {
        Board board = boardRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Board not found"));
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

    public boolean validateBoardForm(BoardForm form, BindingResult result) {
        if (form.isReservation() && (form.getReservationDate() == null || form.getReservationTime() == null)) {
            result.rejectValue("reservationDate", "NotNull", "예약 날짜와 시간을 입력해주세요");
        }
        return result.hasErrors();
    }

    public void prepareBoardListPage(Model model, Pageable pageable, LocalDate date, String email) {
        List<List<LocalDate>> weeks = DateUtils.calculateWeeks(date);
        model.addAttribute("weeks", weeks);

        Page<Board> boards = findAllBoardsByDate(pageable, date);
        Page<ReservationBoard> reservationBoards = reservationBoardService.findAllBoardsByDate(pageable, date);
        Member loginMember = memberRepository.findMemberByEmail(email).orElse(null);


        model.addAttribute("reservationBoards", reservationBoards);
        model.addAttribute("loginMember", loginMember);
        model.addAttribute("boards", boards);
        model.addAttribute("selectedDate", date);
    }

    public void preparePostDetailPage(Long id, Model model, LoginSessionInfo loginSessionInfo) {
        Board board = findOneBoardWithMember(id);
        if (board == null) {
            return;
        }

        boolean isLogin = board.getMember().getEmail().equals(loginSessionInfo.getEmail());
        boolean isManager = loginSessionInfo.getRole() == Role.ADMIN || loginSessionInfo.getRole() == Role.MANAGER
                || loginSessionInfo.getRole() == Role.STAFF;

        List<Reply> comments = replyService.getCommentsByPostId(id);
        PostDetail postDetail = new PostDetail(isLogin, isManager, loginSessionInfo.getEmail(), board, comments);

        model.addAttribute("postDetail", postDetail);
        model.addAttribute("imagePath", board.getImagePath());
    }

    public void prepareEditForm(Long id, Model model, LoginSessionInfo loginSessionInfo) {
        Board board = findOneBoardWithMember(id);
        if (board == null || loginSessionInfo == null) {
            return;
        }
        model.addAttribute("board", board);
    }

    public Page<Board> findAllByMemberEmail (String email, Pageable pageable) {
        return boardRepository.findAllByMemberEmail(email, pageable);
    }

    public boolean isUserAuthorOfBoard(Long boardId, String userEmail) {
        Board board = boardRepository.findById(boardId).orElse(null);
        return board != null && board.getMember().getEmail().equals(userEmail);
    }
}