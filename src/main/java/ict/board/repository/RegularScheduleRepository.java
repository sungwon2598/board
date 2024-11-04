package ict.board.repository;

import ict.board.domain.schedule.RegularSchedule;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegularScheduleRepository extends JpaRepository<RegularSchedule, Long> {
    List<RegularSchedule> findByDayOfWeek(String dayOfWeek);
    List<RegularSchedule> findByClassroom_NameAndDayOfWeek(String name, String dayOfWeek);
}