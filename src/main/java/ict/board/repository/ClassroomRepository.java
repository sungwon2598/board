package ict.board.repository;

import ict.board.domain.schedule.Classroom;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    @Query("SELECT c FROM Classroom c " +
            "WHERE c.id IN (" +
            "    SELECT cs.classroom.id FROM ClassroomSoftware cs " +
            "    WHERE cs.software.id IN :softwareIds " +
            "    GROUP BY cs.classroom.id " +
            "    HAVING COUNT(DISTINCT cs.software.id) = :softwareCount" +
            ") " +
            "AND NOT EXISTS (" +
            "    SELECT 1 FROM c.regularSchedules rs " +
            "    WHERE rs.dayOfWeek = :dayOfWeek " +
            "    AND (rs.startTime < :endTime AND rs.endTime > :startTime)" +
            ") " +
            "AND NOT EXISTS (" +
            "    SELECT 1 FROM c.makeupClasses mc " +
            "    WHERE mc.date = :date " +
            "    AND (mc.startTime < :endTime AND mc.endTime > :startTime)" +
            ")")
    List<Classroom> findAvailableClassrooms(
            @Param("softwareIds") Set<Long> softwareIds,
            @Param("softwareCount") Long softwareCount,
            @Param("date") LocalDate date,
            @Param("dayOfWeek") String dayOfWeek,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    Optional<Classroom> findByName(String name);
}