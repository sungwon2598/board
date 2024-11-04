package ict.board.service.classroom;

import ict.board.domain.schedule.RegularSchedule;
import ict.board.exception.ScheduleConflictException;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleValidationService {

    public void validateScheduleConflicts(RegularSchedule newSchedule, List<RegularSchedule> existingSchedules) {
        log.debug("=== 스케줄 충돌 검사 시작 ===");
        log.debug("신규 스케줄: {} ({} {}~{})",
                newSchedule.getClassName(),
                newSchedule.getDayOfWeek(),
                newSchedule.getStartTime(),
                newSchedule.getEndTime()
        );

        for (RegularSchedule existingSchedule : existingSchedules) {
            log.debug("기존 스케줄 비교: {} ({} {}~{})",
                    existingSchedule.getClassName(),
                    existingSchedule.getDayOfWeek(),
                    existingSchedule.getStartTime(),
                    existingSchedule.getEndTime()
            );

            // 같은 ID를 가진 스케줄은 비교하지 않음 (자기 자신)
            if (existingSchedule.getId() != null && existingSchedule.getId().equals(newSchedule.getId())) {
                log.debug("동일 스케줄 건너뜀 (ID: {})", existingSchedule.getId());
                continue;
            }

            if (isTimeConflict(newSchedule, existingSchedule)) {
                String newTime = String.format("%s ~ %s",
                        newSchedule.getStartTime(),
                        newSchedule.getEndTime()
                );
                String existingTime = String.format("%s ~ %s",
                        existingSchedule.getStartTime(),
                        existingSchedule.getEndTime()
                );

                throw new ScheduleConflictException(
                        newSchedule.getClassName(),
                        existingSchedule.getClassName(),
                        existingSchedule.getClassroom().getName(),
                        existingSchedule.getDayOfWeek(),
                        newTime,
                        existingTime
                );
            }
        }
        log.debug("=== 스케줄 충돌 검사 완료 - 충돌 없음 ===");
    }

    public boolean isTimeConflict(RegularSchedule schedule1, RegularSchedule schedule2) {
        log.debug("시간 충돌 검사: {} vs {}",
                schedule1.getClassName(), schedule2.getClassName());

        // 다른 요일이면 충돌하지 않음
        if (!schedule1.getDayOfWeek().equals(schedule2.getDayOfWeek())) {
            log.debug("다른 요일 - 충돌 없음");
            return false;
        }

        // 다른 강의실이면 충돌하지 않음
        if (!schedule1.getClassroom().getName().equals(schedule2.getClassroom().getName())) {
            log.debug("다른 강의실 - 충돌 없음");
            return false;
        }

        boolean conflict = !(
                schedule1.getEndTime().isBefore(schedule2.getStartTime()) ||
                        schedule1.getStartTime().isAfter(schedule2.getEndTime())
        );

        log.debug("시간 충돌 결과: {}", conflict);
        return conflict;
    }
}
