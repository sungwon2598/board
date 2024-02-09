package ict.board.repsoitory;


import ict.board.domain.board.Board;
import ict.board.domain.member.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepostiory extends JpaRepository<Board, Long> {

    @Query("select b from Board b join fetch b.member where b.id = :id")
    Board findWithMemberById(@Param("id") Long id);

    @Query("select b from Board b join fetch b.member order by b.createdAt desc")
    List<Board> findAllWithMember();

    List<Board> findByMember(Member member);

//    private final EntityManager em;
//
//    public void save(Board board) {
//        em.persist(board);
//    }
//
//    public void deleteOne(Long id) {
//        em.remove(findOne(id));
//    }
//
//    public Board findOne(Long id) {
//        return em.createQuery("select b from Board b join fetch b.member where b.id =:id", Board.class)
//                .setParameter("id", id)
//                .getSingleResult();
//    }
//
//    public List<Board> findAll() {
//        return em.createQuery("select b from Board b join fetch b.member order by b.createdAt desc", Board.class)
//                .getResultList();
//    }
//
//    public List<Board> findBoardsByMember(Member member) {
//        return em.createQuery("select b from Board b where b.member = :member", Board.class)
//                .setParameter("member", member)
//                .getResultList();
//    }

}
