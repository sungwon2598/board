package ict.board.exception;

public class ScheduleConflictException extends RuntimeException {
    public ScheduleConflictException(String className, String classroomName, String dayOfWeek, String time,
                                     String conflictType, String conflictingClassName) {
        super(String.format(
                "강의실 '%s'에 %s요일 %s 시간에 이미 다른 %s이 존재합니다. (충돌 과목: %s)",
                classroomName, dayOfWeek, time, conflictType, conflictingClassName
        ));
    }

    public ScheduleConflictException(String className, String classroomName, String dayOfWeek, String time,
                                     String conflictType) {
        super(String.format(
                "강의실 '%s'에 %s요일 %s 시간에 이미 다른 %s이 존재합니다.",
                classroomName, dayOfWeek, time, conflictType
        ));
    }
}