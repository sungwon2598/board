package ict.board.service;

import ict.board.constant.SessionConst;
import ict.board.domain.member.IctStaffMember;
import ict.board.domain.member.Member;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    public void createLoginSession(HttpSession session, Member member) {
        session.setAttribute(SessionConst.LOGIN_MEMBER, member.getEmail());
        if (member instanceof IctStaffMember) {
            session.setAttribute(SessionConst.MEMBER_ROLE, ((IctStaffMember) member).getRole().name());
        } else {
            session.setAttribute(SessionConst.MEMBER_ROLE, "NONE");
        }
    }

    public void invalidateSession(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }
}