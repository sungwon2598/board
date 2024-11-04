package ict.board.exception;

import lombok.Getter;

@Getter
public class ScheduleNotFoundException extends RuntimeException {
    private final Long scheduleId;

    public ScheduleNotFoundException(Long scheduleId) {
        super(String.format("스케줄을 찾을 수 없습니다. (ID: %d)", scheduleId));
        this.scheduleId = scheduleId;
    }
}