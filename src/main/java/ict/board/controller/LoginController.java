package ict.board.controller;

import ict.board.consts.SessionConst;
import ict.board.config.argumentresolver.Login;
import ict.board.domain.board.Board;
import ict.board.domain.member.Member;
import ict.board.domain.reply.Reply;
import ict.board.dto.LoginForm;
import ict.board.service.BoardService;
import ict.board.service.LoginService;
import ict.board.service.MemberService;
import ict.board.service.ReplyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @GetMapping("login")
    public String login(Model model) {
        model.addAttribute("LoginForm", new LoginForm());
        return "login";
    }

    @PostMapping("login")
    public String myPage(@Valid LoginForm form, BindingResult result, HttpServletRequest request,
                         @RequestParam(defaultValue = "/") String redirectURL) {

        if (result.hasErrors()) {
            return "login";
        }

        String loginMemberEmail = loginService.login(form.getEmail(), form.getPassword()).getEmail();
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMemberEmail);

        return "redirect:" + redirectURL;
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }

}