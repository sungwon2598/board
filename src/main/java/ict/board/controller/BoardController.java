package ict.board.controller;

import ict.board.domain.board.BoardStatus;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
                         @AuthenticationPrincipal UserDetails userDetails) throws IOException, InterruptedException {
        if (userDetails == null) {
            return "redirect:/login";
        }
        if (boardService.validateBoardForm(form, result)) {
            return "board/boardform";
        }

        String imagePath = fileService.saveImage(form.getImage());
        String email = userDetails.getUsername();
        if (form.isReservation()) {
            boardService.saveReservationBoard(form, imagePath, email);
        } else {
            boardService.saveBoard(form, imagePath, email);
        }

        return "redirect:/";
    }

    @GetMapping("/")
    public String listBoards(@AuthenticationPrincipal UserDetails userDetails, Model model,
                             @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        String email = userDetails.getUsername();
        boardService.prepareBoardListPage(model, pageable, LocalDate.now(), email);

        if (userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_NONE"))) {
            return "redirect:/guest/boards";
        }
        return "redirect:/date/" + LocalDate.now();
    }

    @GetMapping("/date/{date}")
    public String listBoardsByDate(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String date, Model model,
                                   @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        if (userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_NONE"))) {
            return "redirect:/guest/boards";
        }

        String email = userDetails.getUsername();
        boardService.prepareBoardListPage(model, pageable, LocalDate.parse(date), email);
        return "Index";
    }

    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN') or @boardService.isUserAuthorOfBoard(#id, principal.username)")
    @GetMapping("/board/{id}")
    public String postDetail(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        boardService.preparePostDetailPage(id, model, userDetails);
        return "board/postDetail";
    }

    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN') or @boardService.isUserAuthorOfBoard(#id, principal.username)")
    @GetMapping("/board/{id}/editForm")
    public String editForm(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        boardService.prepareEditForm(id, model, userDetails);
        return "board/editForm";
    }

    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN') or @boardService.isUserAuthorOfBoard(#id, principal.username)")
    @PostMapping("/board/{id}/edit")
    public String editPost(@PathVariable Long id, String title, String content, String requester,
                           String requesterLocation) {
        boardService.updateBoard(id, title, content, requester, requesterLocation);
        return "redirect:/board/" + id;
    }

    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN') or @boardService.isUserAuthorOfBoard(#id, principal.username)")
    @PostMapping("/board/{id}/delete")
    public String deletePost(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        if (boardService.deleteBoard(id, userDetails)) {
            return "redirect:/";
        } else {
            redirectAttributes.addFlashAttribute("error", "인증 실패");
            return "redirect:/board/" + id;
        }
    }

    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    @PostMapping("/board/{id}/changeStatus")
    public String changeStatus(@PathVariable Long id, String status,
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