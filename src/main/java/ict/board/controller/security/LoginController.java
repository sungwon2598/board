package ict.board.controller.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class LoginController {

    @GetMapping("/login")
    public String loginForm(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String attempts,
                            Model model) {

        if (error != null) {
            if ("blocked".equals(error)) {
                model.addAttribute("blocked", true);
            } else {
                model.addAttribute("loginError", true);
                if (attempts != null) {
                    model.addAttribute("remainingAttempts", Integer.parseInt(attempts));  // String을 Integer로 변환
                }
            }
        }

        return "login";
    }
}