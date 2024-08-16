package ict.board.service;

import ict.board.domain.board.Board;
import ict.board.domain.board.BoardStatus;
import ict.board.domain.board.ReservationBoard;
import ict.board.domain.reply.Reply;
import ict.board.dto.BoardForm;
import ict.board.dto.PostDetail;
import ict.board.exception.BoardNotFoundException;
import ict.board.exception.BoardSaveException;
import ict.board.exception.EntityNotFoundException;
import ict.board.exception.UnauthorizedAccessException;
import ict.board.repository.BoardRepository;
import ict.board.repository.MemberRepository;
import ict.board.service.ai.AIResponseHandler;
import ict.board.service.slack.NewBoardSender;
import ict.board.util.DateUtils;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final ReplyService replyService;
    private final AIResponseHandler aiResponseHandler;
    private final NewBoardSender newBoardSender;
    private final ReservationBoardService reservationBoardService;


    @Transactional
    public Long saveBoard(BoardForm form, String imagePath, String loginMemberEmail) {
        try {
            Board board = new Board(form.getTitle(), form.getContent(), form.getRequester(), form.getRequesterLocation(), imagePath);
            return save(board, loginMemberEmail);
        } catch (IOException | InterruptedException e) {
            log.error("Error saving board: ", e);
            throw new BoardSaveException("게시물 저장 중 오류가 발생했습니다.", e);
        }
    }

    @Transactional
    public Long saveReservationBoard(BoardForm form, String imagePath, String loginMemberEmail) {
        try {
            ReservationBoard reservationBoard = new ReservationBoard(
                    form.getTitle(),
                    form.getContent(),
                    form.getRequester(),
                    form.getRequesterLocation(),
                    LocalDateTime.of(form.getReservationDate(), form.getReservationTime()),
                    imagePath
            );
            return save(reservationBoard, loginMemberEmail);
        } catch (IOException | InterruptedException e) {
            log.error("Error saving reservation board: ", e);
            throw new BoardSaveException("예약 게시물 저장 중 오류가 발생했습니다.", e);
        }
    }

    @Transactional
    protected Long save(Board board, String loginMemberEmail) throws IOException, InterruptedException {
        board.addMember(memberRepository.findMemberByEmail(loginMemberEmail)
                .orElseThrow(() -> new EntityNotFoundException("Member not found: " + loginMemberEmail)));
        boardRepository.save(board);

        String ask = board.getContent();
        newBoardSender.sendFromBoard(board);
        aiResponseHandler.answerGpt(board, ask);
        return board.getId();
    }

//    @Transactional
//    public Long saveBoard(BoardForm form, String imagePath, String loginMemberEmail)
//            throws IOException, InterruptedException {
//        Board board = new Board(form.getTitle(), form.getContent(), form.getRequester(), form.getRequesterLocation(),
//                imagePath);
//        return save(board, loginMemberEmail);
//    }
//
//    @Transactional
//    public Long saveReservationBoard(BoardForm form, String imagePath, String loginMemberEmail)
//            throws IOException, InterruptedException {
//        ReservationBoard reservationBoard = new ReservationBoard(
//                form.getTitle(),
//                form.getContent(),
//                form.getRequester(),
//                form.getRequesterLocation(),
//                LocalDateTime.of(form.getReservationDate(), form.getReservationTime()),
//                imagePath
//        );
//        return save(reservationBoard, loginMemberEmail);
//    }
//
//    @Transactional
//    protected Long save(Board board, String loginMemberEmail) throws IOException, InterruptedException {
//        board.addMember(memberRepository.findMemberByEmail(loginMemberEmail)
//                .orElseThrow(() -> new EntityNotFoundException("Member not found")));
//        boardRepository.save(board);
//
//        String ask = board.getContent();
//        newBoardSender.sendFromBoard(board);
//        AIResponseHandler.answerGpt(board, ask);
//        return board.getId();
//    }

    @Transactional
    public void deleteBoard(Long id, UserDetails userDetails) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardNotFoundException("게시물을 찾을 수 없습니다."));

        if (userDetails == null || !board.getMember().getEmail().equals(userDetails.getUsername())) {
            throw new UnauthorizedAccessException("게시물을 삭제할 권한이 없습니다.");
        }

        replyService.deleteRepliesByBoardId(id);
        boardRepository.deleteById(id);
    }

    @Transactional
    public void changeBoardStatus(Long id, BoardStatus boardStatus) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardNotFoundException("게시물을 찾을 수 없습니다."));
        board.changeStatus(boardStatus);
    }

    @Transactional
    public void updateBoard(Long id, String newTitle, String newContent, String requester, String requesterLocation) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardNotFoundException("게시물을 찾을 수 없습니다."));
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
        return boardRepository.findWithMemberById(id)
                .orElseThrow(() -> new BoardNotFoundException("게시물을 찾을 수 없습니다."));
    }

    public Page<Board> findAllBoardsByStatus(Pageable pageable, String status) {
        BoardStatus boardStatus = BoardStatus.valueOf(status);
        return boardRepository.findAllByBoardStatus(pageable, boardStatus);
    }

    public void prepareBoardListPage(Model model, Pageable pageable, LocalDate date, String email) {
        List<List<LocalDate>> weeks = DateUtils.calculateWeeks(date);
        model.addAttribute("weeks", weeks);

        Page<Board> boards = findAllBoardsByDate(pageable, date);
        Page<ReservationBoard> reservationBoards = reservationBoardService.findAllBoardsByDate(pageable, date);

        model.addAttribute("reservationBoards", reservationBoards);
        model.addAttribute("loginMemberEmail", email);
        model.addAttribute("boards", boards);
        model.addAttribute("selectedDate", date);
    }

    public void preparePostDetailPage(Long id, Model model, UserDetails userDetails) {
        Board board = findOneBoardWithMember(id);

        boolean isLogin = board.getMember().getEmail().equals(userDetails.getUsername());
        boolean isManager = userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) ||
                userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MANAGER")) ||
                userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_STAFF"));

        List<Reply> comments = replyService.getCommentsByPostId(id);
        PostDetail postDetail = new PostDetail(isLogin, isManager, userDetails.getUsername(), board, comments);

        model.addAttribute("postDetail", postDetail);
        model.addAttribute("imagePath", board.getImagePath());
    }

    public void prepareEditForm(Long id, Model model, UserDetails userDetails) {
        Board board = findOneBoardWithMember(id);
        if (userDetails == null) {
            throw new UnauthorizedAccessException("로그인이 필요합니다.");
        }
        model.addAttribute("board", board);
    }

    public Page<Board> findAllByMemberEmail(String email, Pageable pageable) {
        return boardRepository.findAllByMemberEmail(email, pageable);
    }

    public boolean isUserAuthorOfBoard(Long boardId, String userEmail) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("게시물을 찾을 수 없습니다."));
        return board.getMember().getEmail().equals(userEmail);
    }
}