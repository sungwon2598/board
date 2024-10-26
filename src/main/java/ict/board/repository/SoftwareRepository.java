package ict.board.repository;

import ict.board.domain.schedule.Software;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SoftwareRepository extends JpaRepository<Software, Long> {
    List<Software> findAllByOrderByNameAsc();
}