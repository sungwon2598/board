// ScheduleFormUtils.java
package ict.board.util;

import ict.board.dto.request.RegisterRegularScheduleDto;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
public class ScheduleFormUtils {

    public void addScheduleFormAttributes(Model model) {
        model.addAttribute("registerRegularScheduleDto", new RegisterRegularScheduleDto());
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

    public Map<String, String> getTimeSlotsByType(String type) {
        return "night".equals(type) ? getNightTimes() : getDayTimes();
    }

    public boolean isValidTimeSlot(String timeSlot, String type) {
        return getTimeSlotsByType(type).containsKey(timeSlot);
    }

    public boolean isValidClassroom(String classroom) {
        return getClassrooms().contains(classroom);
    }
}