package ict.board.service.classroom;

import ict.board.domain.schedule.Classroom;
import ict.board.domain.schedule.Department;
import ict.board.domain.schedule.RegularSchedule;
import ict.board.repository.ClassroomRepository;
import ict.board.repository.DepartmentRepository;
import ict.board.repository.RegularScheduleRepository;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegularScheduleService {

    private final RegularScheduleRepository regularScheduleRepository;
    private final ClassroomRepository classroomRepository;
    private final DepartmentRepository departmentRepository;
    private final ScheduleValidationService scheduleValidationService;

    @Transactional
    public RegularSchedule registerRegularSchedule(RegularSchedule regularSchedule, String classroomName, String departmentName) {
        log.debug("스케줄 등록 시작: {} / {} / {}", regularSchedule, classroomName, departmentName);

        Classroom classroom = findClassroom(classroomName);
        Department department = findOrCreateDepartment(departmentName);

        // 충돌 검사를 위해 먼저 기존 스케줄 조회
        List<RegularSchedule> existingSchedules = regularScheduleRepository
                .findByClassroom_NameAndDayOfWeek(classroomName, regularSchedule.getDayOfWeek());

        log.debug("기존 스케줄 수: {}", existingSchedules.size());

        // 시간 중복 검증
        scheduleValidationService.validateScheduleConflicts(regularSchedule, existingSchedules);

        // 검증 통과 후 엔티티 연관관계 설정
        regularSchedule.setClassroom(classroom);
        regularSchedule.setDepartment(department);

        // 저장
        return regularScheduleRepository.save(regularSchedule);
    }

    @Transactional(readOnly = true)
    public Classroom findClassroom(String classroomName) {
        return classroomRepository.findByName(classroomName)
                .orElseThrow(() -> new IllegalArgumentException("Classroom not found: " + classroomName));
    }

    @Transactional(readOnly = true)
    public Department findOrCreateDepartment(String departmentName) {
        return departmentRepository.findByName(departmentName)
                .orElseGet(() -> departmentRepository.save(Department.builder().name(departmentName).build()));
    }

    @Transactional(readOnly = true)
    public Map<DayOfWeek, List<RegularSchedule>> getWeeklySchedule() {
        List<RegularSchedule> allSchedules = regularScheduleRepository.findAll();
        Map<DayOfWeek, List<RegularSchedule>> weeklySchedule = new TreeMap<>();

        for (DayOfWeek day : DayOfWeek.values()) {
            weeklySchedule.put(day, new ArrayList<>());
        }

        for (RegularSchedule schedule : allSchedules) {
            DayOfWeek day = DayOfWeek.valueOf(schedule.getDayOfWeek().toUpperCase());
            weeklySchedule.get(day).add(schedule);
        }

        return weeklySchedule;
    }

    public List<RegularSchedule> findByDayOfWeek(String dayOfWeek) {
        return regularScheduleRepository.findByDayOfWeek(dayOfWeek);
    }

    public List<RegularSchedule> findByClassroomNumberAndDayOfWeek(Integer roomNumber, String dayOfWeek) {
        String roomName = String.valueOf(roomNumber);
        return regularScheduleRepository.findByClassroom_NameAndDayOfWeek(roomName, dayOfWeek);
    }
}