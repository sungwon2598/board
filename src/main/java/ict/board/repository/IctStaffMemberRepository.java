package ict.board.repository;

import ict.board.domain.member.IctStaffMember;
import ict.board.domain.member.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IctStaffMemberRepository extends JpaRepository<IctStaffMember, Long> {

    Optional<IctStaffMember> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Member> findAllByRoleIsNull();

    List<Member> findAllByRoleIsNotNull();
}