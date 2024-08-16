package ict.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class BoardForm {

    @NotBlank(message = "제목을 입력해주세요", groups = {ReservationValidation.class, GeneralValidation.class})
    private String title;

    @NotEmpty(message = "내용을 입력해주세요", groups = {ReservationValidation.class, GeneralValidation.class})
    private String content;

    private String requester;

    private String requesterLocation;

    private boolean reservation;

    private MultipartFile image;

    @NotNull(message = "예약 날짜를 입력해주세요", groups = ReservationValidation.class)
    private LocalDate reservationDate;

    @NotNull(message = "예약 시간을 입력해주세요", groups = ReservationValidation.class)
    private LocalTime reservationTime;

}