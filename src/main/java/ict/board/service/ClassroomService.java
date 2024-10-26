package ict.board.service;

import ict.board.domain.schedule.Classroom;
import ict.board.domain.schedule.Software;
import ict.board.dto.ClassroomDto;
import ict.board.repository.ClassroomRepository;
import ict.board.repository.SoftwareRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final SoftwareRepository softwareRepository;

    @Transactional
    public Classroom registerClassroom(ClassroomDto classroomDto) {
        Classroom classroom = Classroom.builder()
                .name(classroomDto.getName())
                .capacity(classroomDto.getCapacity())
                .build();

        if (classroomDto.getSoftwareIds() != null) {
            List<Software> softwares = softwareRepository.findAllById(classroomDto.getSoftwareIds());
            for (Software software : softwares) {
                classroom.addSoftware(software, LocalDate.now());
            }
        }

        return classroomRepository.save(classroom);
    }
}