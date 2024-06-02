package ict.board.repository;

import ict.board.domain.member.IctStaffMember;
import ict.board.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IctStaffMemberRepository extends JpaRepository<IctStaffMember, Long> {

    List<Member> findAllByRoleIsNull();

    List<Member> findAllByRoleIsNotNull();
}