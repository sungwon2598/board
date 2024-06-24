package ict.board.repository;


import ict.board.domain.member.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findMemberByEmail(String email);

    @Query("SELECT m FROM Member m WHERE TYPE(m) = Member")
    List<Member> findAllMembers();

    @Query("SELECT m.name FROM Member m WHERE m.email = :email")
    Optional<String> findMemberNameByEmail(String email);

}