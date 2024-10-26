package ict.board.service;

import ict.board.domain.board.Board;
import ict.board.domain.member.IctStaffMember;
import ict.board.domain.member.Member;
import ict.board.domain.reply.Reply;
import ict.board.dto.MemberInfo;
import ict.board.repository.BoardRepository;
import ict.board.repository.IctStaffMemberRepository;
import ict.board.repository.MemberRepository;
import ict.board.repository.ReplyRepository;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

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
        Member existingMember = memberRepository.findMemberByEmail(member.getEmail()).orElse(null);
        if (existingMember != null) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    public MemberInfo getMemberInfo(String email) {
        Member loginMember = findMemberByEmail(email);

        List<Board> boards = boardRepository.findByMember(loginMember);
        List<Reply> replies = replyRepository.findRepliesByMember(loginMember);

        String memberName = loginMember.getName();
        String memberTeam = loginMember.getTeam();
        String roleString = loginMember.getRole().toString();

        return new MemberInfo(boards, replies, memberName, email, memberTeam, roleString);
    }

//    public Member findMemberByEmail(String email) {
//        return memberRepository.findMemberByEmail(email).orElse(null);
//    }

    public List<Member> getAllMembers() {
        return memberRepository.findAllMembers();
    }

    public Optional<String> findMemberNameByEmail(String email) {
        return memberRepository.findMemberNameByEmail(email);
    }

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        return findMemberByEmail(username);
//    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading user by username: {}", username);
        Member member = findMemberByEmail(username);
        if (member == null) {
            log.error("User not found with email: {}", username);
            throw new UsernameNotFoundException("User not found with email: " + username);
        }
        log.info("User found: {}, Role: {}", username, member.getRole());
        return new org.springframework.security.core.userdetails.User(
                member.getEmail(),
                member.getPassword(),
                getAuthorities(member)
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Member member) {
        Collection<GrantedAuthority> authorities;
        if (member instanceof IctStaffMember) {
            authorities = Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_" + ((IctStaffMember) member).getRole().name()));
        } else {
            authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + member.getRole().name()));
        }
        log.info("Authorities for user {}: {}", member.getEmail(), authorities);
        return authorities;
    }

    public Member findMemberByEmail(String email) {
        log.info("Finding member by email: {}", email);
        Member member = memberRepository.findMemberByEmail(email).orElse(null);
        if (member == null) {
            log.warn("Member not found with email: {}", email);
        } else {
            log.info("Member found: {}, Role: {}", email, member.getRole());
        }
        return member;
    }

}