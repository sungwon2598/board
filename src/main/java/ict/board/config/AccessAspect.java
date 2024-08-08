package ict.board.config;

import ict.board.config.annotation.AuthorOnly;
import ict.board.config.annotation.ICTOnly;
import ict.board.config.annotation.ICTorAuthor;
import ict.board.config.argumentresolver.LoginMemberArgumentResolver.LoginSessionInfo;
import ict.board.constant.SessionConst;
import ict.board.domain.member.Role;
import ict.board.exception.CustomAccessDeniedException;
import ict.board.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class AccessAspect {

    private final BoardService boardService;
    private final HttpServletRequest request;

    @Before("@annotation(ictonly)")
    public void checkICTOnlyAccess(JoinPoint joinPoint, ICTOnly ictonly) {
        checkStaffAccess();
    }

    @Before("@annotation(authorOnly)")
    public void checkAuthorOnlyAccess(JoinPoint joinPoint, AuthorOnly authorOnly) {
        checkAccess(joinPoint, true);
    }

    @Before("@annotation(ictorAuthor)")
    public void checkICTorAuthorAccess(JoinPoint joinPoint, ICTorAuthor ictorAuthor) {
        checkAccess(joinPoint, false);
    }

    private void checkStaffAccess() {
        LoginSessionInfo loginSessionInfo = getSessionInfo();
        if (!isStaff(loginSessionInfo.getRole())) {
            throw new CustomAccessDeniedException("Access denied");
        }
    }

    private void checkAccess(JoinPoint joinPoint, boolean authorOnly) {
        LoginSessionInfo loginSessionInfo = getSessionInfo();
        if (authorOnly && loginSessionInfo.getRole() == Role.NONE && isAuthor(joinPoint, loginSessionInfo.getEmail())) {
            return;
        }

        if (!authorOnly && (isStaff(loginSessionInfo.getRole()) || (loginSessionInfo.getRole() == Role.NONE && isAuthor(joinPoint, loginSessionInfo.getEmail())))) {
            return;
        }

        throw new CustomAccessDeniedException("Access denied");
    }

    private LoginSessionInfo getSessionInfo() {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new CustomAccessDeniedException("No session found");
        }

        String email = (String) session.getAttribute(SessionConst.LOGIN_MEMBER);
        String roleStr = (String) session.getAttribute(SessionConst.MEMBER_ROLE);
        if (email == null || roleStr == null) {
            throw new CustomAccessDeniedException("User not logged in");
        }

        Role role = Role.valueOf(roleStr);
        return new LoginSessionInfo(email, role);
    }

    private boolean isStaff(Role role) {
        return role == Role.STAFF || role == Role.MANAGER || role == Role.ADMIN;
    }

    private boolean isAuthor(JoinPoint joinPoint, String email) {
        Object[] args = joinPoint.getArgs();
        Long boardId = Arrays.stream(args)
                .filter(arg -> arg instanceof Long)
                .map(arg -> (Long) arg)
                .findFirst()
                .orElse(null);

        return boardId != null && boardService.isUserAuthorOfBoard(boardId, email);
    }
}