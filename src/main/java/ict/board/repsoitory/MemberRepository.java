package ict.board.repsoitory;


import ict.board.domain.member.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    public void save(Member member) {
        em.persist(member);
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    public Long findMemberIdByEmail(String email) {
        try {
            return em.createQuery("select m.id from Member m where m.email = :email", Long.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            // 쿼리 결과가 둘 이상일 때의 처리
            throw new IllegalStateException("이메일에 해당하는 멤버가 둘 이상입니다.");
        }
    }

    public Member findMemberByEmail(String email) {
        try {
            return em.createQuery("select m from Member m where m.email = :email", Member.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            // 결과가 없을 때의 처리
            return null; // 또는 적절한 예외를 던지거나 다른 처리를 할 수 있습니다.
        } catch (NonUniqueResultException e) {
            // 쿼리 결과가 둘 이상일 때의 처리
            throw new IllegalStateException("이메일에 해당하는 멤버가 둘 이상입니다.");
        }
    }

    public Member findPasswordByEmail(String email) {
        try {
            return em.createQuery("select m from Member m where m.email = :email", Member.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            throw new IllegalStateException("두개 이상의 이메일이 있습니다");
        }

    }
}
