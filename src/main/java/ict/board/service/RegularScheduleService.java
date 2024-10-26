package ict.board.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegularScheduleService {

    private final RegularScheduleRepository regularScheduleRepository;
    private final ClassroomRepository classroomRepository;
    private final DepartmentRepository departmentRepository;

    @Transactional
    public RegularSchedule registerRegularSchedule(RegularSchedule regularSchedule, String classroomName, String departmentName) {
        Classroom classroom = findClassroom(classroomName);
        Department department = findOrCreateDepartment(departmentName);

        regularSchedule.setClassroom(classroom);
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
}