package ict.board.controller;

import ict.board.constant.SessionConst;
import ict.board.dto.LoginForm;
import ict.board.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "login";
    }

    @PostMapping("/login")
    public String myPage(@Valid @ModelAttribute("loginForm") LoginForm form, BindingResult result, HttpServletRequest request,
                         @RequestParam(defaultValue = "/") String redirectURL, Model model) {

        if (result.hasErrors()) {
            return "login";
        }

        try {
            String loginMemberEmail = loginService.login(form.getEmail(), form.getPassword()).getEmail();
            HttpSession session = request.getSession();
            session.setAttribute(SessionConst.LOGIN_MEMBER, loginMemberEmail);
            return "redirect:" + redirectURL;
        } catch (IllegalArgumentException e) {
            model.addAttribute("loginError", e.getMessage());
            return "login";
        }
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
