package ict.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScheduleConflictDetails {
    private final String newClassName;
    private final String existingClassName;
    private final String classroomName;
    private final String dayOfWeek;
    private final String newScheduleTime;
    private final String existingScheduleTime;
}