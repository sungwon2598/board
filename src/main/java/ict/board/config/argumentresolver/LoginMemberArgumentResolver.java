package ict.board.config.argumentresolver;

import ict.board.constant.SessionConst;
import ict.board.domain.member.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasLoginAnnotation = parameter.hasParameterAnnotation(Login.class);
        boolean hasLoginSessionInfoType = LoginSessionInfo.class.isAssignableFrom(parameter.getParameterType());
        return hasLoginAnnotation && hasLoginSessionInfoType;
    }

    @Override
    public LoginSessionInfo resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                            NativeWebRequest webRequest, WebDataBinderFactory binderFactory)
            throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        String email = (String) session.getAttribute(SessionConst.LOGIN_MEMBER);
        String roleStr = (String) session.getAttribute(SessionConst.MEMBER_ROLE);
        Role role = roleStr != null ? Role.valueOf(roleStr) : Role.NONE;

        return new LoginSessionInfo(email, role);
    }

    public static class LoginSessionInfo {
        private final String email;
        private final Role role;

        public LoginSessionInfo(String email, Role role) {
            this.email = email;
            this.role = role;
        }

        public String getEmail() {
            return email;
        }

        public Role getRole() {
            return role;
        }
    }
}