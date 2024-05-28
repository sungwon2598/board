package ict.board.repository;


import ict.board.domain.board.Board;
import ict.board.domain.board.BoardStatus;
import ict.board.domain.board.ReservationBoard;
import ict.board.domain.member.Member;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("select b from Board b join fetch b.member where b.id = :id")
    Board findWithMemberById(@Param("id") Long id);

    @Query("select b from Board b join fetch b.member order by b.createdAt desc")
    Page<Board> findAllWithMember(Pageable pageable);

    List<Board> findByMember(Member member);

    Page<Board> findAllByCreatedAtBetween(LocalDateTime startCreatedAt, LocalDateTime endCreatedAt, Pageable pageable);

    List<Board> findByBoardStatus(BoardStatus boardStatus);

    List<ReservationBoard> findByBoardStatusAndReservationDateBetween(BoardStatus status, LocalDateTime start, LocalDateTime end);

    List<Board> findByCreatedAtAfter(LocalDateTime dateTime);

    Page<Board> findAllByBoardStatus(Pageable pageable, BoardStatus boardStatus);

}