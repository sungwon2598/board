package ict.board.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardForm {

    @NotEmpty(message = "제목을 입력해주세요")
    private String title;

    @NotEmpty(message = "내용을 입력해주세요")
    private String content;

    private String requester;

    private String requesterLocation;

    // 예약 민원 여부
    private boolean reservation;

    // 예약 날짜 (예약 민원일 경우 필요)
    @NotNull(message = "예약 날짜를 입력해주세요", groups = ReservationValidationGroup.class)
    private LocalDate reservationDate;

    // 예약 시간 (예약 민원일 경우 필요)
    @NotNull(message = "예약 시간을 입력해주세요", groups = ReservationValidationGroup.class)
    private LocalTime reservationTime;

}