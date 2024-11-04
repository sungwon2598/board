package ict.board.controller.schedule;

import ict.board.domain.schedule.MakeupClass;
import ict.board.domain.schedule.RegularSchedule;
import ict.board.service.classroom.MakeupClassService;
import ict.board.service.classroom.RegularScheduleService;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final RegularScheduleService regularScheduleService;
    private final MakeupClassService makeupClassService;

    @GetMapping
    public String viewSchedule(Model model,
                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        // 날짜가 지정되지 않은 경우 오늘 날짜 사용
        LocalDate selectedDate = (date != null) ? date : LocalDate.now();

        // 요일 가져오기 (MONDAY, TUESDAY 등)
        DayOfWeek dayOfWeek = selectedDate.getDayOfWeek();

        // 정규 강의 조회
        List<RegularSchedule> regularSchedules = regularScheduleService.findByDayOfWeek(dayOfWeek.toString());

        // 보강 강의 조회
        List<MakeupClass> makeupClasses = makeupClassService.findByDate(selectedDate);

        // 강의실 층별 리스트 생성
        Map<Integer, List<Integer>> floorRooms = Map.of(
                2, IntStream.rangeClosed(201, 215).boxed().collect(Collectors.toList()),
                3, IntStream.rangeClosed(301, 315).boxed().collect(Collectors.toList()),
                4, IntStream.rangeClosed(410, 413).boxed().collect(Collectors.toList())
        );

        // 시간대 리스트 (9시-18시)
        List<Integer> timeSlots = IntStream.rangeClosed(9, 18).boxed().collect(Collectors.toList());

        // Model에 데이터 추가
        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("regularSchedules", regularSchedules);
        model.addAttribute("makeupClasses", makeupClasses);
        model.addAttribute("floorRooms", floorRooms);
        model.addAttribute("timeSlots", timeSlots);

        return "schedule/view";  // templates/schedule/view.html을 찾음
    }

    // 특정 강의실의 전체 일정을 조회하는 API (선택적 구현)
    @GetMapping("/classroom")
    public String viewClassroomSchedule(Model model,
                                        @RequestParam Integer roomNumber,
                                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        LocalDate selectedDate = (date != null) ? date : LocalDate.now();
        DayOfWeek dayOfWeek = selectedDate.getDayOfWeek();

        List<RegularSchedule> regularSchedules = regularScheduleService
                .findByClassroomNumberAndDayOfWeek(roomNumber, dayOfWeek.toString());
        List<MakeupClass> makeupClasses = makeupClassService
                .findByClassroomNumberAndDate(roomNumber, selectedDate);

        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("roomNumber", roomNumber);
        model.addAttribute("regularSchedules", regularSchedules);
        model.addAttribute("makeupClasses", makeupClasses);

        return "schedule/classroom";  // templates/schedule/classroom.html을 찾음
    }
}