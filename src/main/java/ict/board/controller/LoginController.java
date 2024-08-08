package ict.board.controller;

import ict.board.dto.LoginForm;
import ict.board.dto.LoginResult;
import ict.board.service.LoginService;
import ict.board.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
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
    private final SessionService sessionService;

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginForm") LoginForm form,
                        BindingResult bindingResult,
                        HttpServletRequest request,
                        @RequestParam(defaultValue = "/") String redirectURL,
                        Model model) {

        if (bindingResult.hasErrors()) {
            return "login";
        }

        LoginResult loginResult = loginService.authenticate(form.getEmail(), form.getPassword());

        if (loginResult.isSuccess()) {
            sessionService.createLoginSession(request.getSession(), loginResult.getMember());
            return "redirect:" + redirectURL;
        } else {
            model.addAttribute("loginError", loginResult.getErrorMessage());
            return "login";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        sessionService.invalidateSession(request.getSession(false));
        return "redirect:/login";
    }
}