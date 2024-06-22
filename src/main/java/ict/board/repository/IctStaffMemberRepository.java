package ict.board.repository;

import ict.board.domain.member.IctStaffMember;
import ict.board.domain.member.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IctStaffMemberRepository extends JpaRepository<IctStaffMember, Long> {

    boolean existsByEmail(String email);

    List<Member> findAllByRoleIsNull();

    List<Member> findAllByRoleIsNotNull();
}