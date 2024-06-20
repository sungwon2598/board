package ict.board.service;

import ict.board.repository.IctStaffMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IctStaffMemberService {

    private final IctStaffMemberRepository ictStaffMemberRepository;

    public boolean findIctmemberById(String email) {
        return ictStaffMemberRepository.existsByEmail(email);
    }
}
