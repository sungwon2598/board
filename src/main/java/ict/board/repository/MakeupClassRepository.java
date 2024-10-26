package ict.board.repository;

import ict.board.domain.schedule.MakeupClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MakeupClassRepository extends JpaRepository<MakeupClass, Long> {
    List<MakeupClass> findByDate(LocalDate date);
    List<MakeupClass> findByDateBetween(LocalDate startDate, LocalDate endDate);
    List<MakeupClass> findAllByOrderByDateAscStartTimeAsc();
}