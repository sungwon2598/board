package ict.board.domain.board;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
@Entity
@DiscriminatorValue("Reservation")
public class ReservationBoard extends Board {

    @Column(name = "reservation_date")
    private LocalDateTime reservationDate;

    public ReservationBoard() {
        super();
    }

    public ReservationBoard(String title, String content, String requester, String requseterLocation,  LocalDateTime reservationDate) {
        super(title, content, requester, requseterLocation);
        this.reservationDate = reservationDate;
    }

}