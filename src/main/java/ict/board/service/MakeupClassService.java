package ict.board.service;

import ict.board.domain.schedule.Classroom;
import ict.board.domain.schedule.Department;
import ict.board.domain.schedule.MakeupClass;
import ict.board.dto.request.RegisterMakeupClassDto;
import ict.board.repository.ClassroomRepository;
import ict.board.repository.DepartmentRepository;
import ict.board.repository.MakeupClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class MakeupClassService {

    private final MakeupClassRepository makeupClassRepository;
    private final ClassroomRepository classroomRepository;
    private final DepartmentRepository departmentRepository;

    @Transactional
    public MakeupClass registerMakeupClass(RegisterMakeupClassDto dto) {
        Classroom classroom = findClassroom(dto.getClassroomName());
        Department department = findOrCreateDepartment(dto.getDepartmentName());

        MakeupClass makeupClass = MakeupClass.createMakeupClass(
                classroom,
                department,
                dto.getDate(),
                LocalTime.parse(dto.getStartTime()),
                LocalTime.parse(dto.getEndTime())
        );

        return makeupClassRepository.save(makeupClass);
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
    public List<MakeupClass> getMakeupClassesByDate(LocalDate date) {
        return makeupClassRepository.findByDate(date);
    }

    @Transactional(readOnly = true)
    public List<MakeupClass> getMakeupClassesByDateRange(LocalDate startDate, LocalDate endDate) {
        return makeupClassRepository.findByDateBetween(startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<MakeupClass> getAllMakeupClasses() {
        return makeupClassRepository.findAllByOrderByDateAscStartTimeAsc();
    }

    public MakeupClass getMakeupClassById(Long id) {
        return makeupClassRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid makeup class id: " + id));
    }

    @Transactional
    public MakeupClass updateMakeupClass(Long id, RegisterMakeupClassDto dto) {
        MakeupClass makeupClass = makeupClassRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 보강 일정입니다."));

        Classroom classroom = classroomRepository.findByName(dto.getClassroomName())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의실입니다."));

        Department department = departmentRepository.findByName(dto.getDepartmentName())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 학과입니다."));

        makeupClass.update(
                dto.getDate(),
                LocalTime.parse(dto.getStartTime()),
                LocalTime.parse(dto.getEndTime()),
                classroom,
                department
        );

        return makeupClass;
    }

    public void deleteMakeupClass(Long id) {
        makeupClassRepository.deleteById(id);
    }

    public List<String> getAvailableClassrooms() {
        List<String> classrooms = new ArrayList<>();

        // 201 - 215 (214 제외)
        classrooms.addAll(IntStream.rangeClosed(201, 215)
                .filter(i -> i != 214)
                .mapToObj(String::valueOf)
                .collect(Collectors.toList()));

        // 301 - 315 (314 제외)
        classrooms.addAll(IntStream.rangeClosed(301, 315)
                .filter(i -> i != 314)
                .mapToObj(String::valueOf)
                .collect(Collectors.toList()));

        // 410, 411, 412, 413
        classrooms.addAll(IntStream.rangeClosed(410, 413)
                .mapToObj(String::valueOf)
                .collect(Collectors.toList()));

        return classrooms;
    }
}