package ict.board.controller;

import ict.board.config.argumentresolver.Login;
import ict.board.config.argumentresolver.LoginMemberArgumentResolver.LoginSessionInfo;
import ict.board.domain.board.Board;
import ict.board.domain.board.BoardStatus;
import ict.board.domain.board.ReservationBoard;
import ict.board.domain.member.Member;
import ict.board.domain.member.Role;
import ict.board.domain.reply.Reply;
import ict.board.dto.BoardForm;
import ict.board.dto.PostDetail;
import ict.board.service.BoardService;
import ict.board.service.FileService;
import ict.board.service.IctStaffMemberService;
import ict.board.service.MemberService;
import ict.board.service.ReplyService;
import ict.board.service.ReservationBoardService;
import ict.board.service.ai.WeeklyReportService;
import ict.board.util.DateUtils;
import jakarta.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
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
    private final IctStaffMemberService ictStaffMemberService;
    private final ReservationBoardService reservationBoardService;
    private final WeeklyReportService weeklyReportService;
    private final FileService fileService;

    @GetMapping("board/new")
    public String createBoardForm(Model model) {
        model.addAttribute("BoardForm", new BoardForm());
        return "board/boardform";
    }

    @PostMapping("board/new")
    public String create(@Valid BoardForm form, BindingResult result,
                         @Login LoginSessionInfo loginSessionInfo) throws IOException, InterruptedException {
        if (form.isReservation() && (form.getReservationDate() == null || form.getReservationTime() == null)) {
            result.rejectValue("reservationDate", "NotNull", "예약 날짜와 시간을 입력해주세요");
        }

        if (result.hasErrors()) {
            return "board/boardform";
        }

        String imagePath = null;
        if (!form.getImage().isEmpty()) {
            imagePath = fileService.saveImage(form.getImage());
        }

        if (form.isReservation()) {
            boardService.saveReservationBoard(form, imagePath, loginSessionInfo.getEmail());
        } else {
            boardService.saveBoard(form, imagePath, loginSessionInfo.getEmail());
        }

        return "redirect:/";
    }

    @GetMapping("/")
    public String listBoards(@Login LoginSessionInfo loginSessionInfo, Model model,
                             @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        LocalDate today = LocalDate.now();
        List<List<LocalDate>> weeks = DateUtils.calculateWeeks(today);

        model.addAttribute("weeks", weeks);

        Page<Board> boards = boardService.findAllBoardsByDate(pageable, today);
        Page<ReservationBoard> reservationBoards = reservationBoardService.findAllBoardsByDate(pageable, today);

        Member loginMember = memberService.findMemberByEmail(loginSessionInfo.getEmail());
        model.addAttribute("reservationBoards", reservationBoards);
        model.addAttribute("loginMember", loginMember);
        model.addAttribute("boards", boards);

        return "redirect:/date/" + today;
    }

    @GetMapping("/date/{date}")
    public String listBoardsByDate(@Login LoginSessionInfo loginSessionInfo, @PathVariable String date, Model model,
                                   @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        LocalDate selectedDate = LocalDate.parse(date);
        List<List<LocalDate>> weeks = DateUtils.calculateWeeks(selectedDate);

        model.addAttribute("weeks", weeks);

        Page<Board> boards = boardService.findAllBoardsByDate(pageable, selectedDate);
        Page<ReservationBoard> reservationBoards = reservationBoardService.findAllBoardsByDate(pageable, selectedDate);
        Member loginMember = memberService.findMemberByEmail(loginSessionInfo.getEmail());

        model.addAttribute("loginMember", loginMember);
        model.addAttribute("reservationBoards", reservationBoards);
        model.addAttribute("boards", boards);
        model.addAttribute("selectedDate", selectedDate);

        return "Index";
    }

    @GetMapping("/board/{id}")
    public String postDetail(@PathVariable Long id, Model model, @Login LoginSessionInfo loginSessionInfo) {
        Board board = boardService.findOneBoardWithMember(id);

        if (board == null) {
            return "redirect:/";
        }

        boolean isLogin = board.getMember().getEmail().equals(loginSessionInfo.getEmail());
        boolean isManager = loginSessionInfo.getRole() == Role.ADMIN || loginSessionInfo.getRole() == Role.MANAGER
                || loginSessionInfo.getRole() == Role.STAFF;

        List<Reply> comments = replyService.getCommentsByPostId(id);

        PostDetail postDetail = new PostDetail(isLogin, isManager, loginSessionInfo.getEmail(), board, comments);
        model.addAttribute("postDetail", postDetail);
        model.addAttribute("imagePath", board.getImagePath());

        return "board/postDetail";
    }

    @GetMapping("/board/{id}/editForm")
    public String editForm(@Login LoginSessionInfo loginSessionInfo, @PathVariable Long id, Model model) {
        Board board = boardService.findOneBoardWithMember(id);

        if (board == null || loginSessionInfo == null) {
            return "redirect:/board/" + id + "?error=auth";
        }

        model.addAttribute("board", board);
        return "board/editForm";
    }

    @PostMapping("/board/{id}/edit")
    public String editPost(@PathVariable Long id, String title, String content, String requester,
                           String requesterLocation) {
        boardService.updateBoard(id, title, content, requester, requesterLocation);
        return "redirect:/board/" + id;
    }

    @PostMapping("/board/{id}/delete")
    public String deletePost(@Login LoginSessionInfo loginSessionInfo, @PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        if (boardService.deleteBoard(id, loginSessionInfo)) {
            return "redirect:/";
        } else {
            redirectAttributes.addFlashAttribute("error", "인증 실패");
            return "redirect:/board/" + id;
        }
    }

    @PostMapping("/board/{id}/changeStatus")
    public String changeStatus(@Login LoginSessionInfo loginSessionInfo, @PathVariable Long id, String status,
                               RedirectAttributes redirectAttributes) {
        if (boardService.changeBoardStatus(id, BoardStatus.valueOf(status))) {
            return "redirect:/board/" + id;
        } else {
            redirectAttributes.addFlashAttribute("error", "인증 실패");
            return "redirect:/board/" + id;
        }
    }

    @GetMapping("/weekly")
    public CompletableFuture<String> getWeeklyReport() {
        return weeklyReportService.getWeeklyReport();
    }
}