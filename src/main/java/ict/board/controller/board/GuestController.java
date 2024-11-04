package ict.board.controller.board;

import ict.board.domain.board.Board;
import ict.board.dto.GusetInfo;
import ict.board.service.board.BoardService;
import ict.board.service.MemberService;
import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/guest/*")
public class GuestController {

    private final BoardService boardService;
    private final MemberService memberService;

    @GetMapping("/boards")
    public String listBoards(@AuthenticationPrincipal UserDetails userDetails, Model model,
                             @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        String email = userDetails.getUsername();
        boardService.prepareBoardListPage(model, pageable, LocalDate.now(), email);
        Optional<String> memberName = memberService.findMemberNameByEmail(email);
        Page<Board> allByMemberEmail = boardService.findAllByMemberEmail(email, pageable);
        GusetInfo guestInfo = new GusetInfo(memberName, allByMemberEmail);
        model.addAttribute("guestInfo", guestInfo);

        return "guest";
    }
}