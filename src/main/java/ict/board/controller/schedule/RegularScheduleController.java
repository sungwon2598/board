package ict.board.controller.schedule;

import ict.board.domain.schedule.RegularSchedule;
import ict.board.dto.request.RegisterRegularScheduleDto;
import ict.board.service.classroom.RegularScheduleService;
import ict.board.util.ScheduleFormUtils;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/regular-schedules")
@RequiredArgsConstructor
public class RegularScheduleController {

    private final RegularScheduleService regularScheduleService;
    private final ScheduleFormUtils scheduleFormUtils;

    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        scheduleFormUtils.addScheduleFormAttributes(model);
        return "regular-schedules/register";
    }

    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<String> registerRegularSchedule(@ModelAttribute RegisterRegularScheduleDto dto) {
        try {
            String[] startTimes = dto.getStartTime().split("~");
            String[] endTimes = dto.getEndTime().split("~");

            LocalTime startTime = LocalTime.parse(startTimes[0].trim());
            LocalTime endTime = LocalTime.parse(endTimes[1].trim());

            if (endTime.isBefore(startTime)) {
                return ResponseEntity.badRequest()
                        .body("<div data-error-message=\"종료 시간이 시작 시간보다 빠를 수 없습니다.\"></div>");
            }

            RegularSchedule regularSchedule = RegularSchedule.builder()
                    .dayOfWeek(dto.getDayOfWeek())
                    .startTime(startTime)
                    .endTime(endTime)
                    .className(dto.getClassName())
                    .classSection(dto.getClassSection())
                    .professorName(dto.getProfessorName())
                    .build();

            regularScheduleService.registerRegularSchedule(regularSchedule, dto.getClassroomName(),
                    dto.getDepartmentName());

            return ResponseEntity.ok().build();

        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest()
                    .body("<div data-error-message=\"시간 형식이 올바르지 않습니다.\"></div>");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("<div data-error-message=\"" + e.getMessage() + "\"></div>");
        }
    }
}