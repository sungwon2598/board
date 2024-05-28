package ict.board.controller;

import ict.board.config.argumentresolver.Login;
import ict.board.domain.board.Board;
import ict.board.domain.board.BoardStatus;
import ict.board.domain.board.ReservationBoard;
import ict.board.domain.member.IctStaffMember;
import ict.board.domain.member.Member;
import ict.board.domain.reply.Reply;
import ict.board.dto.BoardForm;
import ict.board.dto.PostDetail;
import ict.board.service.BoardService;
import ict.board.service.MemberService;
import ict.board.service.ReplyService;
import ict.board.service.ReservationBoardService;
import ict.board.service.ai.WeeklyReportService;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    private final BoardService boardService;
    private final ReplyService replyService;
    private final MemberService memberService;
    private final ReservationBoardService reservationBoardService;
    private final WeeklyReportService weeklyReportService;

    @GetMapping("board/new")
    public String createBoardForm(Model model) {
        model.addAttribute("BoardForm", new BoardForm());
        return "board/boardform";
    }

    @PostMapping("/board/new")
    public String create(BoardForm form, BindingResult result,
                         @Login String loginMemberEmail) throws IOException, InterruptedException {
        if (form.isReservation()) {
            // 예약민원인 경우 날짜와 시간이 입력되지 않았다면 에러 추가
            if (form.getReservationDate() == null) {
                result.rejectValue("reservationDate", "NotNull", "예약 날짜를 입력해주세요");
            }
            if (form.getReservationTime() == null) {
                result.rejectValue("reservationTime", "NotNull", "예약 시간을 입력해주세요");
            }
        }
        if (result.hasErrors()) {
            return "board/new";
        }

        if (form.isReservation()) {
            if (form.getReservationDate() == null || form.getReservationTime() == null) {
                result.rejectValue("reservationDate", "NotNull", "예약 날짜와 시간을 입력해주세요");
                return "board/new";
            }
            ReservationBoard reservationBoard = new ReservationBoard(
                    form.getTitle(),
                    form.getContent(),
                    form.getRequester(),
                    form.getRequesterLocation(),
                    LocalDateTime.of(form.getReservationDate(), form.getReservationTime())
            );
            boardService.save(reservationBoard, loginMemberEmail);
        } else {
            Board board = new Board(form.getTitle(), form.getContent(), form.getRequester(),
                    form.getRequesterLocation());
            boardService.save(board, loginMemberEmail);
        }

        return "redirect:/";
    }

    @GetMapping("/")
    public String listBoards(
            @Login String loginMemberEmail,
            Model model, @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC)
            Pageable pageable) {

        LocalDate today = LocalDate.now();
        List<List<LocalDate>> weeks = calculateWeeks(today);

        model.addAttribute("weeks", weeks);

        Page<Board> boards = boardService.findAllBoardsByDate(pageable, today);
        Page<ReservationBoard> reservationBoards = reservationBoardService.findAllBoardsByDate(pageable, today);

        Member loginMember = memberService.findMemberByEmail(loginMemberEmail);
        model.addAttribute("reservationBoards", reservationBoards);

        model.addAttribute("loginMember", loginMember);
        model.addAttribute("boards", boards);
        return "Index";
    }

    @GetMapping("/date/{date}")
    public String listBoardsByDate(
            @Login String loginMemberEmail, @PathVariable String date,
            Model model, @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC)
            Pageable pageable) {

        LocalDate selectedDate = LocalDate.parse(date);
        List<List<LocalDate>> weeks = calculateWeeks(selectedDate);

        model.addAttribute("weeks", weeks);

        Page<Board> boards = boardService.findAllBoardsByDate(pageable, selectedDate);
        Page<ReservationBoard> reservationBoards = reservationBoardService.findAllBoardsByDate(pageable, selectedDate);
        Member loginMember = memberService.findMemberByEmail(loginMemberEmail);

        model.addAttribute("loginMember", loginMember);
        model.addAttribute("reservationBoards", reservationBoards);
        model.addAttribute("boards", boards);
        return "Index";
    }

    private List<List<LocalDate>> calculateWeeks(LocalDate date) {
        LocalDate start = date.withDayOfMonth(1);
        LocalDate end = date.withDayOfMonth(date.lengthOfMonth());

        start = start.minusDays(start.getDayOfWeek().getValue() % 7);

        List<List<LocalDate>> weeks = new ArrayList<>();
        List<LocalDate> currentWeek = new ArrayList<>();
        weeks.add(currentWeek);

        for (LocalDate currentDate = start; currentDate.isBefore(end.plusDays(1));
             currentDate = currentDate.plusDays(1)) {
            if (currentDate.getDayOfWeek() == DayOfWeek.SUNDAY && !currentWeek.isEmpty()) {
                currentWeek = new ArrayList<>();
                weeks.add(currentWeek);
            }
            currentWeek.add(currentDate);
        }

        return weeks;
    }

    @GetMapping("/board/{id}")
    public String postDetail(@PathVariable Long id, Model model, @Login String loginMemberEmail) {
        Board board = boardService.findOneBoard(id);

        if (board == null) {
            return "redirect:/";
        }

        boolean isLogin = board.getMember().getEmail().equals(loginMemberEmail);
        Member loginMember = memberService.findMemberByEmail(loginMemberEmail);
        boolean isManager = false;
        if (loginMember instanceof IctStaffMember) {
            isManager = true;
        }
        List<Reply> comments = replyService.getCommentsByPostId(id);

        PostDetail postDetail = new PostDetail(isLogin, isManager, loginMemberEmail, board, comments);
        model.addAttribute("postDetail", postDetail);

        return "board/postDetail";
    }

    @GetMapping("/board/{id}/editForm")
    public String editForm(@Login String loginMemberEmail, @PathVariable Long id, Model model) {

        Board board = boardService.findOneBoard(id);

        if (board == null || loginMemberEmail == null) {
            return "redirect:/board/" + id + "?error=auth";
        }

        model.addAttribute("board", board);
        return "board/editForm";
    }

    @PostMapping("/board/{id}/edit")
    public String editPost(@PathVariable Long id, String title, String content, String requester,
                           String requesterLocation) {
        Board board = boardService.findOneBoard(id);

        if (board == null) {
            return "redirect:/";
        }
        boardService.update(id, title, content, requester, requesterLocation);
        return "redirect:/board/" + id;
    }

    @PostMapping("/board/{id}/delete")
    public String deletePost(@Login String loginMemberEmail, @PathVariable Long id,
                             RedirectAttributes redirectAttributes) {

        Board board = boardService.findOneBoard(id);

        if (board == null || loginMemberEmail == null) {
            redirectAttributes.addFlashAttribute("error", "인증 실패");
            return "redirect:/board/" + id;
        }

        List<Reply> commentsByPostId = replyService.getCommentsByPostId(id);
        for (Reply reply : commentsByPostId) {
            replyService.deleteReply(reply.getId());
        } //동적 쿼리 한방으로 하게 못하나?
        boardService.delete(id);
        return "redirect:/";
    }

    @PostMapping("/board/{id}/changeStatus")
    public String changeStatus(@PathVariable Long id, String adminPassword, String status,
                               RedirectAttributes redirectAttributes) {

        BoardStatus boardStatus = BoardStatus.valueOf(status);

        boolean success = boardService.changeBoardStatus(id, boardStatus, adminPassword);

        if (!success) {
            redirectAttributes.addFlashAttribute("error", "인증 실패");
            return "redirect:/board/" + id;
        }

        return "redirect:/board/" + id;
    }

    @GetMapping("/weekly")
    public CompletableFuture<String> getWeeklyReport() {
        return weeklyReportService.getWeeklyReport();
    }

}