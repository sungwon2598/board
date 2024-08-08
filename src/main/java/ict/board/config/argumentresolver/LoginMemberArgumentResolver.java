package ict.board.config.argumentresolver;

import ict.board.config.annotation.Login;
import ict.board.constant.SessionConst;
import ict.board.domain.member.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {
    private static final Logger logger = LoggerFactory.getLogger(LoginMemberArgumentResolver.class);
    private static final Role DEFAULT_ROLE = Role.NONE;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasLoginAnnotation = parameter.hasParameterAnnotation(Login.class);
        boolean hasLoginSessionInfoType = LoginSessionInfo.class.isAssignableFrom(parameter.getParameterType());
        return hasLoginAnnotation && hasLoginSessionInfoType;
    }

    @Override
    public LoginSessionInfo resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        HttpSession session = request.getSession(false);
        if (session == null) {
            logger.debug("Session is null");
            return null;
        }

        String email = (String) session.getAttribute(SessionConst.LOGIN_MEMBER);
        String roleStr = (String) session.getAttribute(SessionConst.MEMBER_ROLE);

        if (email == null) {
            logger.debug("Email is null in session");
            return null;
        }

        Role role = DEFAULT_ROLE;
        if (roleStr != null) {
            try {
                role = Role.valueOf(roleStr);
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid role string in session: {}", roleStr, e);
            }
        }

        logger.debug("Resolved LoginSessionInfo: email={}, role={}", email, role);
        return new LoginSessionInfo(email, role);
    }

    public static class LoginSessionInfo {
        private final String email;
        private final Role role;

        public LoginSessionInfo(String email, Role role) {
            this.email = Objects.requireNonNull(email, "Email cannot be null");
            this.role = Objects.requireNonNull(role, "Role cannot be null");
        }

        public String getEmail() {
            return email;
        }

        public Role getRole() {
            return role;
        }

        @Override
        public String toString() {
            return "LoginSessionInfo{" +
                    "email='" + email + '\'' +
                    ", role=" + role +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LoginSessionInfo that = (LoginSessionInfo) o;
            return Objects.equals(email, that.email) && role == that.role;
        }

        @Override
        public int hashCode() {
            return Objects.hash(email, role);
        }
    }
}