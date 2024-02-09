package ict.board.repsoitory;


import ict.board.domain.board.Board;
import ict.board.domain.member.Member;
import ict.board.service.AiClient;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BoardRepostiory {

    private final EntityManager em;
    private final Logger logger = LoggerFactory.getLogger(AiClient.class);

    public void save(Board board) {
        logger.info(String.valueOf(em.getClass()));
        em.persist(board);
    }

    public void deleteOne(Long id) {
        em.remove(findOne(id));
    }

    public Board findOne(Long id) {
        return em.createQuery("select b from Board b join fetch b.member where b.id =:id", Board.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    public List<Board> findAll() {
        return em.createQuery("select b from Board b join fetch b.member order by b.createdAt desc", Board.class)
                .getResultList();
    }

    public List<Board> findBoardsMyMember(Member member) {
        return em.createQuery("select b from Board b where b.member = :member", Board.class)
                .setParameter("member", member)
                .getResultList();
    }

}
