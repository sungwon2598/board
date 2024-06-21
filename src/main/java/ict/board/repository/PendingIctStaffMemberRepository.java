package ict.board.repository;

import ict.board.domain.member.PendingIctStaffMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PendingIctStaffMemberRepository extends JpaRepository<PendingIctStaffMember, Long> {
}
