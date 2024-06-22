package ict.board.service;

import ict.board.domain.member.IctStaffMember;
import ict.board.repository.IctStaffMemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IctStaffMemberService {

    private final IctStaffMemberRepository ictStaffMemberRepository;

    public List<IctStaffMember> findAll() {
        return ictStaffMemberRepository.findAll();
    }

    @Transactional
    public void joinIctmember(IctStaffMember ictStaffMember) {
        ictStaffMemberRepository.save(ictStaffMember);
    }

}