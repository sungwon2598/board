package ict.board.repository;

import ict.board.domain.schedule.RegularSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegularScheduleRepository extends JpaRepository<RegularSchedule, Long> {
}