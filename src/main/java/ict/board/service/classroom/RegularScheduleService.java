package ict.board.service.classroom;

import ict.board.domain.schedule.Classroom;
import ict.board.domain.schedule.Department;
import ict.board.domain.schedule.MakeupClass;
import ict.board.domain.schedule.RegularSchedule;
import ict.board.exception.ScheduleConflictException;
import ict.board.repository.ClassroomRepository;
import ict.board.repository.DepartmentRepository;
import ict.board.repository.MakeupClassRepository;
import ict.board.repository.RegularScheduleRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
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
    private final MakeupClassRepository makeupClassRepository;
    private final ClassroomRepository classroomRepository;
    private final DepartmentRepository departmentRepository;

    private static final int MONTHS_TO_CHECK = 6;  // 앞으로 6개월간 체크

    @Transactional
    public RegularSchedule registerRegularSchedule(RegularSchedule regularSchedule, String classroomName, String departmentName) {
        // 강의실과 학과 정보 조회
        Classroom classroom = classroomRepository.findByName(classroomName)
                .orElseThrow(() -> new IllegalArgumentException("Classroom not found: " + classroomName));

        Department department = departmentRepository.findByName(departmentName)
                .orElseGet(() -> departmentRepository.save(Department.builder().name(departmentName).build()));

        // 정규 수업 시간 충돌 검사 (기존 코드와 동일)
        List<RegularSchedule> conflictingSchedules = regularScheduleRepository.findConflictingSchedules(
                classroomName,
                regularSchedule.getDayOfWeek(),
                regularSchedule.getId(),
                regularSchedule.getStartTime(),
                regularSchedule.getEndTime()
        );

        if (!conflictingSchedules.isEmpty()) {
            RegularSchedule conflictingSchedule = conflictingSchedules.get(0);
            throw new ScheduleConflictException(
                    regularSchedule.getClassName(),
                    classroomName,
                    regularSchedule.getDayOfWeek(),
                    regularSchedule.getStartTime() + " ~ " + regularSchedule.getEndTime(),
                    "정규 수업",
                    conflictingSchedule.getClassName()
            );
        }

        // 앞으로 6개월간의 해당 요일 보강 수업 충돌 검사
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(MONTHS_TO_CHECK);

        // 요일을 숫자로 변환 (SUNDAY=1, MONDAY=2, ..., SATURDAY=7)
        DayOfWeek dayOfWeek = DayOfWeek.valueOf(regularSchedule.getDayOfWeek().toUpperCase());
        int dayOfWeekValue = dayOfWeek.getValue() % 7 + 1;  // Java의 DayOfWeek를 SQL의 DAYOFWEEK 함수 값으로 변환

        List<MakeupClass> conflictingMakeups = makeupClassRepository.findConflictingFutureMakeupClasses(
                classroomName,
                startDate,
                endDate,
                dayOfWeekValue,
                regularSchedule.getStartTime(),
                regularSchedule.getEndTime()
        );

        if (!conflictingMakeups.isEmpty()) {
            MakeupClass firstConflict = conflictingMakeups.get(0);
            throw new ScheduleConflictException(
                    regularSchedule.getClassName(),
                    classroomName,
                    regularSchedule.getDayOfWeek(),
                    regularSchedule.getStartTime() + " ~ " + regularSchedule.getEndTime(),
                    String.format("보강 수업 (날짜: %s, 과목: %s, 학과: %s, 시간: %s ~ %s)",
                            firstConflict.getDate(),
                            firstConflict.getId(),
                            firstConflict.getDepartment().getName(),
                            firstConflict.getStartTime(),
                            firstConflict.getEndTime())
            );
        }

        // 엔티티 연관관계 설정 및 저장
        regularSchedule.changeClassroom(classroom);
        regularSchedule.setDepartment(department);

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