package ict.board.config.security;

import ict.board.service.security.LoginAttemptService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final LoginAttemptService loginAttemptService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String email = request.getParameter("username");
        loginAttemptService.loginFailed(email);

        int remainingAttempts = loginAttemptService.getRemainingAttempts(email);

        if (loginAttemptService.isBlocked(email)) {
            response.sendRedirect("/login?error=blocked");
        } else {
            response.sendRedirect("/login?error=true&attempts=" + remainingAttempts);
        }
    }
}