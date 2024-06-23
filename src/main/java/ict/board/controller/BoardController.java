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
import ict.board.service.IctStaffMemberService;
import ict.board.service.MemberService;
import ict.board.service.ReplyService;
import ict.board.service.ReservationBoardService;
import ict.board.service.ai.WeeklyReportService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
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

    @Value("${file.upload-dir}")
    private String uploadDir;

    @GetMapping("board/new")
    public String createBoardForm(Model model) {
        model.addAttribute("BoardForm", new BoardForm());
        return "board/boardform";
    }

    @PostMapping("board/new")
    public String create(@Valid BoardForm form, BindingResult result,
                         @Login LoginSessionInfo loginSessionInfo) throws IOException, InterruptedException {
        if (form.isReservation()) {
            if (form.getReservationDate() == null || form.getReservationTime() == null) {
                result.rejectValue("reservationDate", "NotNull", "예약 날짜와 시간을 입력해주세요");
            }
        }

        if (result.hasErrors()) {
            return "board/boardform";
        }

        String imagePath = null;
        if (!form.getImage().isEmpty()) {
            imagePath = saveImage(form.getImage());
        }

        if (form.isReservation()) {
            ReservationBoard reservationBoard = new ReservationBoard(
                    form.getTitle(),
                    form.getContent(),
                    form.getRequester(),
                    form.getRequesterLocation(),
                    LocalDateTime.of(form.getReservationDate(), form.getReservationTime()),
                    imagePath
            );
            boardService.save(reservationBoard, loginSessionInfo.getEmail());
        } else {
            Board board = new Board(form.getTitle(), form.getContent(), form.getRequester(),
                    form.getRequesterLocation(), imagePath);
            boardService.save(board, loginSessionInfo.getEmail());
        }

        return "redirect:/";
    }

    private String saveImage(MultipartFile image) throws IOException {
        if (image.isEmpty()) {
            return null;
        }

        String originalFilename = image.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFilename = System.currentTimeMillis() + fileExtension;
        Path imagePath = Paths.get(uploadDir, newFilename);
        Files.createDirectories(imagePath.getParent());
        Files.write(imagePath, image.getBytes());

        return newFilename;
    }

    @GetMapping("/")
    public String listBoards(
            @Login LoginSessionInfo loginSessionInfo,
            Model model, @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC)
            Pageable pageable) {

        LocalDate today = LocalDate.now();
        List<List<LocalDate>> weeks = calculateWeeks(today);

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
    public String listBoardsByDate(
            @Login LoginSessionInfo loginSessionInfo, @PathVariable String date,
            Model model, @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC)
            Pageable pageable) {

        LocalDate selectedDate = LocalDate.parse(date);
        List<List<LocalDate>> weeks = calculateWeeks(selectedDate);

        model.addAttribute("weeks", weeks);

        Page<Board> boards = boardService.findAllBoardsByDate(pageable, selectedDate);
        Page<ReservationBoard> reservationBoards = reservationBoardService.findAllBoardsByDate(pageable, selectedDate);
        Member loginMember = memberService.findMemberByEmail(loginSessionInfo.getEmail());

        model.addAttribute("loginMember", loginMember);
        model.addAttribute("reservationBoards", reservationBoards);
        model.addAttribute("boards", boards);
        model.addAttribute("selectedDate", selectedDate);  // Add this line
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
    public String postDetail(@PathVariable Long id, Model model, @Login LoginSessionInfo loginSessionInfo) {
        Board board = boardService.findOneBoardWithMember(id);

        if (board == null) {
            return "redirect:/";
        }

        boolean isLogin = board.getMember().getEmail().equals(loginSessionInfo.getEmail());
        log.info(loginSessionInfo.getRole().toString());
        boolean isManager = loginSessionInfo.getRole() == Role.ADMIN ||
                loginSessionInfo.getRole() == Role.MANAGER ||
                loginSessionInfo.getRole() == Role.STAFF;

        List<Reply> comments = replyService.getCommentsByPostId(id);

        PostDetail postDetail = new PostDetail(isLogin, isManager, loginSessionInfo.getEmail(), board, comments);
        model.addAttribute("postDetail", postDetail);
        model.addAttribute("imagePath", board.getImagePath()); // 이미지 경로 추가

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
        Board board = boardService.findOneBoardWithMember(id);

        if (board == null) {
            return "redirect:/";
        }
        boardService.update(id, title, content, requester, requesterLocation);
        return "redirect:/board/" + id;
    }

    @PostMapping("/board/{id}/delete")
    public String deletePost(@Login LoginSessionInfo loginSessionInfo, @PathVariable Long id,
                             RedirectAttributes redirectAttributes) {

        Board board = boardService.findOneBoardWithMember(id);

        if (board == null || loginSessionInfo == null) {
            redirectAttributes.addFlashAttribute("error", "인증 실패");
            return "redirect:/board/" + id;
        }

        replyService.deleteRepliesByBoardId(id);
        boardService.delete(id);
        return "redirect:/";
    }

    @PostMapping("/board/{id}/changeStatus")
    public String changeStatus(@Login LoginSessionInfo loginSessionInfo, @PathVariable Long id, String status,
                               RedirectAttributes redirectAttributes) {

        BoardStatus boardStatus = BoardStatus.valueOf(status);

        boolean success = boardService.changeBoardStatus(id, boardStatus);

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