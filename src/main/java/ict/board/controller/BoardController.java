package ict.board.controller;

import ict.board.domain.board.BoardStatus;
import ict.board.dto.BoardForm;
import ict.board.dto.GeneralValidation;
import ict.board.dto.ReservationValidation;
import ict.board.exception.UnauthorizedAccessException;
import ict.board.service.BoardService;
import ict.board.service.FileService;
import ict.board.service.ai.WeeklyReportService;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
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
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
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
        model.addAttribute("boardForm", new BoardForm());
        return "board/boardform";
    }

    @PostMapping("board/new/general")
    public String createGeneral(@Validated(GeneralValidation.class) BoardForm form,
                                BindingResult result,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model) {
        return processForm(form, result, userDetails, model, false);
    }

    @PostMapping("board/new/reservation")
    public String createReservation(@Validated(ReservationValidation.class) BoardForm form,
                                    BindingResult result,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    Model model) {
        return processForm(form, result, userDetails, model, true);
    }

    private String processForm(BoardForm form, BindingResult result, UserDetails userDetails, Model model,
                               boolean isReservation) {
        model.addAttribute("boardForm", form);

        if (result.hasErrors()) {
            List<String> errorMessages = result.getAllErrors()
                    .stream()
                    .map(error -> {
                        if (error instanceof FieldError) {
                            return (error.getDefaultMessage());
                        }
                        return error.getDefaultMessage();
                    })
                    .collect(Collectors.toList());
            model.addAttribute("errorMessages", errorMessages);
            return "board/boardform";
        }

        String imagePath = null;
        if (!form.getImage().isEmpty()) {
            imagePath = fileService.saveImage(form.getImage());
        }


        String email = userDetails.getUsername();
        Long boardId;
        //try {
        if (isReservation) {
            boardId = boardService.saveReservationBoard(form, imagePath, email);
        } else {
            boardId = boardService.saveBoard(form, imagePath, email);
        }
//        } catch (Exception e) {
//            model.addAttribute("errorMessages", List.of("게시물 저장 중 오류가 발생했습니다."));
//            return "board/boardform";
//        }

        model.addAttribute("successMessage", "게시물이 성공적으로 등록되었습니다.");
        return "redirect:/board/" + boardId;
    }


    @GetMapping("/")
    public String listBoards(@AuthenticationPrincipal UserDetails userDetails, Model model,
                             @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        if (userDetails == null) {
            throw new UnauthorizedAccessException("로그인이 필요합니다.");
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
    public String listBoardsByDate(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String date,
                                   Model model,
                                   @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        if (userDetails == null) {
            throw new UnauthorizedAccessException("로그인이 필요합니다.");
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
            throw new UnauthorizedAccessException("로그인이 필요합니다.");
        }
        boardService.preparePostDetailPage(id, model, userDetails);
        return "board/postDetail";
    }

    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN') or @boardService.isUserAuthorOfBoard(#id, principal.username)")
    @GetMapping("/board/{id}/editForm")
    public String editForm(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id, Model model) {
        if (userDetails == null) {
            throw new UnauthorizedAccessException("로그인이 필요합니다.");
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
            throw new UnauthorizedAccessException("로그인이 필요합니다.");
        }
        boardService.deleteBoard(id, userDetails);
        return "redirect:/";
    }

    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    @PostMapping("/board/{id}/changeStatus")
    public String changeStatus(@PathVariable Long id, String status,
                               RedirectAttributes redirectAttributes) {
        boardService.changeBoardStatus(id, BoardStatus.valueOf(status));
        return "redirect:/board/" + id;
    }

    @GetMapping("/weekly")
    public CompletableFuture<String> getWeeklyReport() {
        return weeklyReportService.getWeeklyReport();
    }
}