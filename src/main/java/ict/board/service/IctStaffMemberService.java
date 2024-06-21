package ict.board.service;

import ict.board.domain.member.IctStaffMember;
import ict.board.repository.IctStaffMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IctStaffMemberService {

    private final IctStaffMemberRepository ictStaffMemberRepository;

    public boolean findIctmemberById(String email) {
        return ictStaffMemberRepository.existsByEmail(email);
    }

    @Transactional
    public void joinIctmember(IctStaffMember ictStaffMember) {
        ictStaffMemberRepository.save(ictStaffMember);
    }
}
