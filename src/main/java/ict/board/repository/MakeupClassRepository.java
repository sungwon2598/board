package ict.board.repository;

import ict.board.domain.schedule.MakeupClass;
import java.time.LocalTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MakeupClassRepository extends JpaRepository<MakeupClass, Long> {
    List<MakeupClass> findByDate(LocalDate date);
    List<MakeupClass> findByDateBetween(LocalDate startDate, LocalDate endDate);
    List<MakeupClass> findAllByOrderByDateAscStartTimeAsc();
    List<MakeupClass> findByClassroom_NameAndDate(String name, LocalDate date);

    @Query("""
            SELECT COUNT(m) > 0 FROM MakeupClass m
            WHERE m.classroom.name = :classroomName
            AND m.date >= :currentDate
            AND NOT (m.endTime <= :startTime OR m.startTime >= :endTime)
            AND m.date IN (
                SELECT DISTINCT mc.date 
                FROM MakeupClass mc 
                WHERE mc.classroom.name = :classroomName 
                AND mc.date >= :currentDate
            )
            """)
    boolean existsConflictingMakeupClass(
            @Param("classroomName") String classroomName,
            @Param("currentDate") LocalDate currentDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    @Query("SELECT m FROM MakeupClass m " +
            "WHERE m.classroom.name = :classroomName " +
            "AND m.date >= :startDate " +  // 시작일 이후
            "AND m.date <= :endDate " +    // 종료일 이전
            "AND FUNCTION('DAYOFWEEK', m.date) = :dayOfWeek " + // 해당 요일만
            "AND ((m.startTime <= :endTime AND m.endTime >= :startTime))")
    List<MakeupClass> findConflictingFutureMakeupClasses(
            @Param("classroomName") String classroomName,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("dayOfWeek") int dayOfWeek,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );
}