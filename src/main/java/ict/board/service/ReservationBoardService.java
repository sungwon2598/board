package ict.board.service;

import ict.board.domain.board.ReservationBoard;
import ict.board.repository.ReservationBoardRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationBoardService {

    private final ReservationBoardRepository reservationBoardRepository;

    public Page<ReservationBoard> findAllBoardsByDate(Pageable pageable, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        return reservationBoardRepository.findAllByReservationDateBetween(startOfDay, endOfDay, pageable);
    }

}
