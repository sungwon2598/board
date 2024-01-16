package ict.board.repsoitory;

import ict.board.domain.reply.Reply;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
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

    public List<Reply> findBoardsReplies(Long boardId) {
        return em.createQuery("select r from Reply r where board.id = :boardId", Reply.class)
                .setParameter("boardId", boardId)
                .getResultList();
    }

    public Reply findOne(Long id) {
        try {
            return em.createQuery("select r from Reply r where id = :id", Reply.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            throw new IllegalStateException("댓글이 2개 이상입니다. 오류");
        }
    }
}
