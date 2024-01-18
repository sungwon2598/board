package ict.board.service;

import ict.board.domain.member.Member;
import ict.board.repsoitory.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public Long join(Member member) {
        validateDuplicate(member);
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicate(Member member) {
        Long memberIdByEmail = memberRepository.findMemberIdByEmail(member.getEmail());
        if (memberIdByEmail != null) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    public Member findMemberByEmail(String email) {
        return memberRepository.findMemberByEmail(email);
    }

}
