package ict.board.service;

import ict.board.domain.board.Board;
import ict.board.domain.member.IctStaffMember;
import ict.board.domain.member.Member;
import ict.board.domain.member.Role;
import ict.board.domain.reply.Reply;
import ict.board.dto.MemberInfo;
import ict.board.repository.BoardRepository;
import ict.board.repository.IctStaffMemberRepository;
import ict.board.repository.MemberRepository;
import ict.board.repository.ReplyRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final IctStaffMemberRepository ictStaffMemberRepository;
    private final BoardRepository boardRepository;
    private final ReplyRepository replyRepository;

    @Transactional
    public Long join(Member member) {
        validateDuplicate(member);
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicate(Member member) {
        Member member1 = memberRepository.findMemberByEmail(member.getEmail()).orElse(null);
        if (member1 != null) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    public MemberInfo getMemberInfo(String email) {
        Member loginMember = findMemberByEmail(email);

        List<Board> boards = boardRepository.findByMember(loginMember);
        List<Reply> replies = replyRepository.findRepliesByMember(loginMember);

        String memberName = loginMember.getName();
        String memberTeam = loginMember.getTeam();

        if (loginMember instanceof IctStaffMember loginIctStaffMember) {
            return new MemberInfo(boards, replies, memberName, email, memberTeam, loginIctStaffMember.getRole());
        }
        return new MemberInfo(boards, replies, memberName, email, memberTeam, Role.NONE);
    }

    public Member findMemberByEmail(String email) {
        return memberRepository.findMemberByEmail(email).orElse(null);
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAllMembers();
    }

}
