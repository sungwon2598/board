package ict.board.controller;

import ict.board.config.argumentresolver.Login;
import ict.board.config.argumentresolver.LoginMemberArgumentResolver.LoginSessionInfo;
import ict.board.domain.board.Board;
import ict.board.dto.GusetInfo;
import ict.board.service.BoardService;
import ict.board.service.MemberService;
import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
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
    public String listBoards(@Login LoginSessionInfo loginSessionInfo, Model model,
                             @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {

        boardService.prepareBoardListPage(model, pageable, LocalDate.now(), loginSessionInfo.getEmail());
        Optional<String> memberName = memberService.findMemberNameByEmail(loginSessionInfo.getEmail());
        Page<Board> allByMemberEmail = boardService.findAllByMemberEmail(loginSessionInfo.getEmail(), pageable);
        GusetInfo guestInfo = new GusetInfo(memberName, allByMemberEmail);
        model.addAttribute("guestInfo", guestInfo);

        return "guest";
    }

}
