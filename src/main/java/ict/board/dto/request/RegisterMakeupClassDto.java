package ict.board.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterMakeupClassDto {

    @NotNull(message = "보강 날짜를 선택해주세요.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;

    @NotBlank(message = "시작 시간을 입력해주세요.")
    private String startTime;

    @NotBlank(message = "종료 시간을 입력해주세요.")
    private String endTime;

    @NotBlank(message = "강의실을 선택해주세요.")
    private String classroomName;

    @NotBlank(message = "학과를 입력해주세요.")
    private String departmentName;

    private List<Long> requestedSoftwareIds;
}