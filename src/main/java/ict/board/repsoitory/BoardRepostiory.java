package ict.board.repsoitory;


import ict.board.domain.board.Board;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import java.time.LocalDateTime;
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

    public void deleteOne(Long id) {
        em.createQuery("delete from Board b where id =:id")
                .setParameter("id", id)
                .executeUpdate();
    }

    public Board findOne(Long id) {
        return em.find(Board.class, id);
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

    public void updateBoard(Long boardId, String newTitle, String newContent) {
        LocalDateTime newlastModifiedTime = LocalDateTime.now();

        em.createQuery(
                        "UPDATE Board b SET b.title = :newTitle, b.content = :newContent, b.lastModifiedAt = :newlastModifiedTime WHERE b.id = :boardId")
                .setParameter("newTitle", newTitle)
                .setParameter("newContent", newContent)
                .setParameter("newlastModifiedTime", newlastModifiedTime)
                .setParameter("boardId", boardId)
                .executeUpdate();
    }

}
