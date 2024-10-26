package ict.board.controller.schedule;


import ict.board.domain.schedule.RegularSchedule;
import ict.board.dto.request.RegisterRegularScheduleDto;
import ict.board.service.RegularScheduleService;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/regular-schedules")
@RequiredArgsConstructor
public class RegularScheduleController {

    private final RegularScheduleService regularScheduleService;

    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerRegularScheduleDto", new RegisterRegularScheduleDto());
        addCommonAttributes(model);
        return "regular-schedules/register";
    }

    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    @PostMapping("/register")
    public String registerRegularSchedule(@ModelAttribute RegisterRegularScheduleDto dto) {
        RegularSchedule regularSchedule = RegularSchedule.builder()
                .dayOfWeek(dto.getDayOfWeek())
                .startTime(LocalTime.parse(dto.getStartTime().split("~")[0]))
                .endTime(LocalTime.parse(dto.getEndTime().split("~")[1]))
                .className(dto.getClassName())
                .classSection(dto.getClassSection())
                .professorName(dto.getProfessorName())
                .build();

        regularScheduleService.registerRegularSchedule(regularSchedule, dto.getClassroomName(),
                dto.getDepartmentName());

        return "redirect:/regular-schedules";
    }

    private void addCommonAttributes(Model model) {
        model.addAttribute("dayTimeTypes", getDayTimeTypes());
        model.addAttribute("dayTimes", getDayTimes());
        model.addAttribute("nightTimes", getNightTimes());
        model.addAttribute("classrooms", getClassrooms());
        model.addAttribute("daysOfWeek", getDaysOfWeek());
    }

    private List<String> getDaysOfWeek() {
        return Arrays.asList("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY");
    }

    private Map<String, String> getDayTimeTypes() {
        Map<String, String> types = new LinkedHashMap<>();
        types.put("day", "주간");
        types.put("night", "야간");
        return types;
    }

    private Map<String, String> getDayTimes() {
        Map<String, String> times = new LinkedHashMap<>();
        times.put("09:00~09:50", "1교시(09:00～09:50)");
        times.put("10:00~10:50", "2교시(10:00～10:50)");
        times.put("11:00~11:50", "3교시(11:00～11:50)");
        times.put("12:00~12:50", "4교시(12:00～12:50)");
        times.put("13:00~13:50", "5교시(13:00～13:50)");
        times.put("14:00~14:50", "6교시(14:00～14:50)");
        times.put("15:00~15:50", "7교시(15:00～15:50)");
        times.put("16:00~16:50", "8교시(16:00～16:50)");
        times.put("17:00~17:50", "9교시(17:00～17:50)");
        times.put("18:00~18:50", "10교시(18:00～18:50)");
        return times;
    }

    private Map<String, String> getNightTimes() {
        Map<String, String> times = new LinkedHashMap<>();
        times.put("17:30~18:15", "1교시(17:30～18:15)");
        times.put("18:15~19:00", "2교시(18:15～19:00)");
        times.put("19:05~19:50", "3교시(19:05～19:50)");
        times.put("19:50~20:35", "4교시(19:50～20:35)");
        times.put("20:40~21:25", "5교시(20:40～21:25)");
        times.put("21:25~22:10", "6교시(21:25～22:10)");
        return times;
    }

    private List<String> getClassrooms() {
        List<String> classrooms = new ArrayList<>();

        // 201 - 215 (214 제외)
        classrooms.addAll(IntStream.rangeClosed(201, 215)
                .filter(i -> i != 214)
                .mapToObj(String::valueOf)
                .collect(Collectors.toList()));

        // 301 - 315 (314 제외)
        classrooms.addAll(IntStream.rangeClosed(301, 315)
                .filter(i -> i != 314)
                .mapToObj(String::valueOf)
                .collect(Collectors.toList()));

        // 410, 411, 412, 413
        classrooms.addAll(IntStream.rangeClosed(410, 413)
                .mapToObj(String::valueOf)
                .collect(Collectors.toList()));

        return classrooms;
    }
}