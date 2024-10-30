package ict.board.controller.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class SessionController {

    @GetMapping("/session-expired")
    public String sessionExpired(Model model) {
        model.addAttribute("message", "다른 기기에서 로그인이 감지되어 현재 세션이 만료되었습니다.");
        return "error/sessionExpired";
    }
}