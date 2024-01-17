package ict.board.controller.login;

import ict.board.domain.board.Board;
import ict.board.domain.member.Member;
import ict.board.domain.reply.Reply;
import ict.board.service.BoardService;
import ict.board.service.LoginService;
import ict.board.service.ReplyService;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final ReplyService replyService;
    private final BoardService boardService;

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("LoginForm", new LoginForm());
        return "/login";
    }

    @PostMapping("/login")
    public String myPage(@Valid LoginForm form, BindingResult result , Model model) {

        if (result.hasErrors()) {
            return "/login";
        }
        Member memeber = loginService.login(form.getEmail(), form.getPassword());
        List<Board> boards = boardService.findBoardsbyMember(memeber);

        List<Reply> replies = replyService.getCommentsByMember(memeber);

        model.addAttribute("boards", boards);
        model.addAttribute("replies", replies);
        return "/members/mypage";
    }
}
