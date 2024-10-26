package ict.board.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;


@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomAvailabilityRequest {

    @NotEmpty(message = "적어도 하나의 소프트웨어를 선택해야 합니다.")
    private Set<Long> softwareIds;

    @NotNull(message = "날짜를 선택해야 합니다.")
    @FutureOrPresent(message = "현재 또는 미래의 날짜만 선택 가능합니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @NotNull(message = "시작 시간을 선택해야 합니다.")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @NotNull(message = "종료 시간을 선택해야 합니다.")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime endTime;

}