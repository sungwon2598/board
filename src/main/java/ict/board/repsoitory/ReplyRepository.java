package ict.board.repsoitory;

import ict.board.domain.member.Member;
import ict.board.domain.reply.Reply;
import ict.board.service.AiClient;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReplyRepository {


    private final EntityManager em;
    private final Logger logger = LoggerFactory.getLogger(AiClient.class);

    public Long save(Reply reply) {
        logger.info(String.valueOf(em.getClass()));
        logger.info(String.valueOf(System.identityHashCode(em)));
        em.persist(reply);
        return reply.getId();
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
        return em.createQuery("select r from Reply r join fetch r.board b join fetch r.member where b.id = :boardId", Reply.class)
                .setParameter("boardId", boardId)
                .getResultList();
    }


    public void updateReply(Long replyId, String newContent) {
        em.createQuery("update Reply r set r.content = :newContent where r.id =:replyId")
                .setParameter("newContent", newContent)
                .setParameter("replyId", replyId)
                .executeUpdate();
    }

}
