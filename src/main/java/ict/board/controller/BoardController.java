package ict.board.controller;

import ict.board.config.argumentresolver.Login;
import ict.board.config.argumentresolver.LoginMemberArgumentResolver.LoginSessionInfo;
import ict.board.domain.board.BoardStatus;
import ict.board.domain.member.Role;
import ict.board.dto.BoardForm;
import ict.board.service.BoardService;
import ict.board.service.FileService;
import ict.board.service.ai.WeeklyReportService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        if (boardService.validateBoardForm(form, result)) {
            return "board/boardform";
        }

        String imagePath = fileService.saveImage(form.getImage());
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
        boardService.prepareBoardListPage(model, pageable, LocalDate.now(), loginSessionInfo.getEmail());
        if(loginSessionInfo.getRole() == Role.NONE) {
            return "redirect:/guest/boards";
        }
        return "redirect:/date/" + LocalDate.now();
    }

    @GetMapping("/date/{date}")
    public String listBoardsByDate(@Login LoginSessionInfo loginSessionInfo, @PathVariable String date, Model model,
                                   @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        boardService.prepareBoardListPage(model, pageable, LocalDate.parse(date), loginSessionInfo.getEmail());
        return "Index";
    }

    @GetMapping("/board/{id}")
    public String postDetail(@PathVariable Long id, Model model, @Login LoginSessionInfo loginSessionInfo) {
        boardService.preparePostDetailPage(id, model, loginSessionInfo);
        return "board/postDetail";
    }

    @GetMapping("/board/{id}/editForm")
    public String editForm(@Login LoginSessionInfo loginSessionInfo, @PathVariable Long id, Model model) {
        boardService.prepareEditForm(id, model, loginSessionInfo);
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