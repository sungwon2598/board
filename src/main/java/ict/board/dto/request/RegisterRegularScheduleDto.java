package ict.board.dto.request;

import java.time.LocalTime;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RegisterRegularScheduleDto {
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private String className;
    private String classSection;
    private String professorName;
    private String classroomName;
    private String departmentName;

    public LocalTime getParseStartTime() {
        String[] times = startTime.split("~");
        return LocalTime.parse(times[0].trim());
    }

    public LocalTime getParseEndTime() {
        String[] times = endTime.split("~");
        return LocalTime.parse(times[1].trim());
    }

    public boolean isValidTimeRange() {
        try {
            LocalTime start = getParseStartTime();
            LocalTime end = getParseEndTime();
            return !end.isBefore(start);
        } catch (Exception e) {
            return false;
        }
    }
}