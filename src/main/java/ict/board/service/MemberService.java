package ict.board.service;

import ict.board.domain.board.Board;
import ict.board.domain.member.Member;
import ict.board.domain.reply.Reply;
import ict.board.dto.MemberInfo;
import ict.board.repsoitory.MemberRepository;
import ict.board.service.ai.OpenAIApiConnector;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BoardService boardService;
    private final ReplyService replyService;

    private final Logger logger = LoggerFactory.getLogger(OpenAIApiConnector.class);

    @Transactional
    public Long join(Member member) {
        logger.info(String.valueOf(memberRepository.getClass()));
        validateDuplicate(member);
        memberRepository.save(member);
        return member.getId();
    }

    public MemberInfo getMemberInfo(String email) {
        Member loginMember = findMemberByEmail(email);

        List<Board> boards = boardService.findBoardsbyMember(loginMember);
        List<Reply> replies = replyService.getCommentsByMember(loginMember);

        String memberName = loginMember.getName();
        String memberTeam = loginMember.getTeam();

        return new MemberInfo(boards, replies, memberName, email, memberTeam);
    }

    private void validateDuplicate(Member member) {
        Member member1 = memberRepository.findMemberByEmail(member.getEmail()).orElse(null);
        if ( member1 != null) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    public Member findMemberByEmail(String email) {
        return memberRepository.findMemberByEmail(email).orElse(null);
    }

}
