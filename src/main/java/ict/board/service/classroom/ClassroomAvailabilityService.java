package ict.board.service.classroom;

import ict.board.domain.schedule.Classroom;
import ict.board.repository.ClassroomRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ClassroomAvailabilityService {
    private final ClassroomRepository classroomRepository;

    public ClassroomAvailabilityService(ClassroomRepository classroomRepository) {
        this.classroomRepository = classroomRepository;
    }

    public List<Classroom> findAvailableClassrooms(Set<Long> softwareIds, LocalDate date, LocalTime startTime, LocalTime endTime) {
        String dayOfWeek = date.getDayOfWeek().name();
        Long softwareCount = (long) softwareIds.size();
        return classroomRepository.findAvailableClassrooms(softwareIds, softwareCount, date, dayOfWeek, startTime, endTime);
    }
}