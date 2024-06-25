package ict.board.config;

import ict.board.config.annotation.OnlyIctAndAuthor;
import ict.board.config.argumentresolver.LoginMemberArgumentResolver.LoginSessionInfo;
import ict.board.constant.SessionConst;
import ict.board.domain.member.Role;
import ict.board.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
public class BoardAuthorAccessAspect {

    private final BoardService boardService;
    private final HttpServletRequest request;

    @Before("@annotation(onlyIctAndAuthor)")
    public void checkRoleBasedAccess(JoinPoint joinPoint, OnlyIctAndAuthor onlyIctAndAuthor) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new SecurityException("No session found");
        }

        String email = (String) session.getAttribute(SessionConst.LOGIN_MEMBER);
        String roleStr = (String) session.getAttribute(SessionConst.MEMBER_ROLE);
        if (email == null || roleStr == null) {
            throw new SecurityException("User not logged in");
        }

        Role role = Role.valueOf(roleStr);
        LoginSessionInfo loginSessionInfo = new LoginSessionInfo(email, role);

        Role userRole = loginSessionInfo.getRole();

        boolean hasAccess = Arrays.stream(onlyIctAndAuthor.roles())
                .anyMatch(role2 -> userRole == role2);

        if (hasAccess) {
            return;
        }

        if (userRole == Role.NONE) {
            Object[] args = joinPoint.getArgs();
            Long boardId = Arrays.stream(args)
                    .filter(arg -> arg instanceof Long)
                    .map(arg -> (Long) arg)
                    .findFirst()
                    .orElse(null);

            if (boardId != null && boardService.isUserAuthorOfBoard(boardId, loginSessionInfo.getEmail())) {
                return;
            }
        }

        throw new SecurityException("Access denied");
    }
}