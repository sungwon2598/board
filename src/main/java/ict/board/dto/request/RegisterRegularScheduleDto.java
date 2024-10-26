package ict.board.dto.request;

import lombok.Data;

@Data
public class RegisterRegularScheduleDto {
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private String className;
    private String classSection;
    private String professorName;
    private String classroomName;
    private String departmentName;
}