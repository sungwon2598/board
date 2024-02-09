package ict.board.service;

import ict.board.domain.member.Member;
import ict.board.repsoitory.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;

    public Member login(String email, String password) {
        Member member = memberRepository.findMemberByEmail(email).orElse(null);
        if (member == null) {
            throw new IllegalStateException("회원이 존재하지 않습니다.");
        }
        if (!member.getPassword().equals(password)) {
            throw new IllegalStateException("비밀번호를 다시 입력해주세요.");
        }
        return member;
    }

}

