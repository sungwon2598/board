package ict.board.repsoitory;


import ict.board.domain.board.Board;
import ict.board.domain.board.BoardStatus;
import ict.board.domain.member.Member;
import ict.board.service.AiClient;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
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
        return em.find(Board.class, id);
    }

    public List<Board> findAll() {
        return em.createQuery("select b from Board b order by b.createdAt desc", Board.class)
                .getResultList();
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

    public void updateStatus(Long id, BoardStatus status) {
        em.createQuery("update Board b set b.boardStatus = :status where b.id =: id")
                .setParameter("status", status)
                .setParameter("id", id)
                .executeUpdate();
    }

    public List<Board> findBoardsMyMember(Member member) {
        return em.createQuery("select b from Board b where b.member = :member", Board.class)
                .setParameter("member", member)
                .getResultList();
    }

}
