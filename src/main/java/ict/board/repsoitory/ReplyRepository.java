package ict.board.repsoitory;

import ict.board.domain.member.Member;
import ict.board.domain.reply.Reply;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReplyRepository {

    private final EntityManager em;

    public void save(Reply reply) {
        em.persist(reply);
    }

    public List<Reply> findRepliesByMember(Member member) {
        return em.createQuery("select r from Reply r where member = :member", Reply.class)
                .setParameter("member", member)
                .getResultList();
    }

    public void deleteAll(List<Long> ids) {
        em.createQuery("DELETE FROM Reply r WHERE r.id IN :ids")
                .setParameter("ids", ids)
                .executeUpdate();
    }

    public void deleteOne(Long id) {
        em.createQuery("DELETE FROM Reply r WHERE r.id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

    public List<Reply> findBoardsReplies(Long boardId) {
        return em.createQuery("select r from Reply r where board.id = :boardId", Reply.class)
                .setParameter("boardId", boardId)
                .getResultList();
    }


}
