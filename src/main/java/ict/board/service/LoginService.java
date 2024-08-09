package ict.board.service;

import ict.board.domain.member.Member;
import ict.board.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;

    public Member login(String email, String password) {
        Member member = memberRepository.findMemberByEmail(email).orElseThrow(() ->
                new IllegalArgumentException("회원이 존재하지 않습니다.")
        );

        if (!BCrypt.checkpw(password, member.getPassword())) {
            throw new IllegalArgumentException("비밀번호를 다시 입력해주세요.");
        }
        return member;
    }

}