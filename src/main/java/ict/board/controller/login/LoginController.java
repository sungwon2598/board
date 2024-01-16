package ict.board.controller.login;

import ict.board.service.LoginService;
import jakarta.validation.Valid;
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

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("LoginForm", new LoginForm());
        return "/login";
    }

    @PostMapping("/login")
    public String create(@Valid LoginForm form, BindingResult result) {

        if (result.hasErrors()) {
            return "/login";
        }

        loginService.login(form.getEmail(), form.getPassword());
        return "redirect:/";
    }
}
