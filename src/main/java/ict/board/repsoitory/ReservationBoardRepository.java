package ict.board.repsoitory;

import ict.board.domain.board.ReservationBoard;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationBoardRepository extends JpaRepository<ReservationBoard, Long> {

    Page<ReservationBoard> findAllByReservationDateBetween(LocalDateTime startDateTime, LocalDateTime endDateTime,
                                                           Pageable pageable);

}
