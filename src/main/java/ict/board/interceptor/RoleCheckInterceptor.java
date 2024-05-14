package ict.board.interceptor;

import ict.board.consts.SessionConst;
import ict.board.domain.member.IctStaffMember;
import ict.board.domain.member.Member;
import ict.board.domain.member.Role;
import ict.board.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
@RequiredArgsConstructor
public class RoleCheckInterceptor implements HandlerInterceptor {

    private final MemberService memberService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
            response.sendRedirect("/login");
            return false;
        }

        String email = (String) session.getAttribute(SessionConst.LOGIN_MEMBER);
        Member member = memberService.findMemberByEmail(email);
        String uri = request.getRequestURI();

        if (member instanceof IctStaffMember) {
            IctStaffMember staffMember = (IctStaffMember) member;
            if (staffMember.getRole() == Role.ADMIN && (uri.startsWith("/admin") || uri.startsWith("/manage"))) {
                return true;
            } else if (staffMember.getRole() == Role.MANAGER && uri.startsWith("/manage")) {
                return true;
            } else {
                response.sendRedirect("/access-denied-not-manage");
                return false;
            }
        }

        response.sendRedirect("/access-denied");
        return false;
    }
}