package ict.board.controller.schedule;

import ict.board.domain.schedule.RegularSchedule;
import ict.board.service.RegularScheduleService;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class WeeklyScheduleController {

    private final RegularScheduleService regularScheduleService;

    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    @GetMapping("/weekly-schedule")
    public String showWeeklySchedule(Model model) {
        Map<DayOfWeek, List<RegularSchedule>> weeklySchedule = regularScheduleService.getWeeklySchedule();
        model.addAttribute("weeklySchedule", weeklySchedule);
        model.addAttribute("timeSlots", generateTimeSlots());
        return "weekly-schedule";
    }

    private List<LocalTime> generateTimeSlots() {
        List<LocalTime> timeSlots = new ArrayList<>();
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(18, 0);

        while (startTime.isBefore(endTime)) {
            timeSlots.add(startTime);
            startTime = startTime.plusMinutes(30);
        }
        return timeSlots;
    }
}