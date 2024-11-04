package ict.board.exception;

import lombok.Getter;

@Getter
public class ScheduleConflictException extends RuntimeException {
    private final String newClassName;
    private final String existingClassName;
    private final String classroomName;
    private final String dayOfWeek;
    private final String newScheduleTime;
    private final String existingScheduleTime;

    public ScheduleConflictException(
            String newClassName,
            String existingClassName,
            String classroomName,
            String dayOfWeek,
            String newScheduleTime,
            String existingScheduleTime
    ) {
        super(String.format(
                "강의 시간이 중복됩니다.\n" +
                        "신규 강의: %s (%s)\n" +
                        "기존 강의: %s (%s)\n" +
                        "강의실: %s, 요일: %s",
                newClassName,
                newScheduleTime,
                existingClassName,
                existingScheduleTime,
                classroomName,
                dayOfWeek
        ));

        this.newClassName = newClassName;
        this.existingClassName = existingClassName;
        this.classroomName = classroomName;
        this.dayOfWeek = dayOfWeek;
        this.newScheduleTime = newScheduleTime;
        this.existingScheduleTime = existingScheduleTime;
    }
}