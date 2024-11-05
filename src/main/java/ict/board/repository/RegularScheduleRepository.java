package ict.board.repository;

import ict.board.domain.schedule.RegularSchedule;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RegularScheduleRepository extends JpaRepository<RegularSchedule, Long> {
    List<RegularSchedule> findByDayOfWeek(String dayOfWeek);

    @Query("""
            SELECT COUNT(rs) > 0 FROM RegularSchedule rs
            WHERE rs.classroom.name = :classroomName
            AND rs.dayOfWeek = :dayOfWeek
            AND (:scheduleId IS NULL OR rs.id != :scheduleId)
            AND NOT (rs.endTime <= :startTime OR rs.startTime >= :endTime)
            """)
    boolean existsConflictingSchedule(
            @Param("classroomName") String classroomName,
            @Param("dayOfWeek") String dayOfWeek,
            @Param("scheduleId") Long scheduleId,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    // 충돌 검사용 쿼리 추가
    @Query("""
            SELECT rs FROM RegularSchedule rs 
            WHERE rs.classroom.name = :classroomName 
            AND rs.dayOfWeek = :dayOfWeek 
            AND (:scheduleId IS NULL OR rs.id != :scheduleId)
            AND NOT (rs.endTime <= :startTime OR rs.startTime >= :endTime)
            """)
    List<RegularSchedule> findConflictingSchedules(
            @Param("classroomName") String classroomName,
            @Param("dayOfWeek") String dayOfWeek,
            @Param("scheduleId") Long scheduleId,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    List<RegularSchedule> findByClassroom_NameAndDayOfWeek(String name, String dayOfWeek);
}