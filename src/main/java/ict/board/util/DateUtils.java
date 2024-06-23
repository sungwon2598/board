package ict.board.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DateUtils {

    public static List<List<LocalDate>> calculateWeeks(LocalDate date) {
        LocalDate start = date.withDayOfMonth(1);
        LocalDate end = date.withDayOfMonth(date.lengthOfMonth());

        start = start.minusDays(start.getDayOfWeek().getValue() % 7);

        List<List<LocalDate>> weeks = new ArrayList<>();
        List<LocalDate> currentWeek = new ArrayList<>();
        weeks.add(currentWeek);

        for (LocalDate currentDate = start; currentDate.isBefore(end.plusDays(1));
             currentDate = currentDate.plusDays(1)) {
            if (currentDate.getDayOfWeek() == DayOfWeek.SUNDAY && !currentWeek.isEmpty()) {
                currentWeek = new ArrayList<>();
                weeks.add(currentWeek);
            }
            currentWeek.add(currentDate);
        }

        return weeks;
    }
}