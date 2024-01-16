package ict.board.repsoitory;


import ict.board.domain.board.Board;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BoardRepostiory {

    private final EntityManager em;

    public void save(Board board) {
        em.persist(board);
    }

    public List<Board> findAll() {
        return em.createQuery("select b from Board b order by b.createdAt desc", Board.class)
                .getResultList();
    }

    public Board findBoardById(Long id) {
        try {
            return em.createQuery("select b from Board b where b.id = :id", Board.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            throw new IllegalStateException("게시글이 2개이상 잡힘 오류");
        }
    }

}
