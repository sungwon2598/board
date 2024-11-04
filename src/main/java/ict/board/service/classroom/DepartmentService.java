package ict.board.service.classroom;

import ict.board.domain.member.Member;
import ict.board.domain.schedule.Department;
import ict.board.dto.request.DepartmentDTO;
import ict.board.repository.DepartmentRepository;
import ict.board.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void createDepartment(DepartmentDTO departmentDTO) {
        Optional<Member> member = Optional.empty();

        if (departmentDTO.getMemberId() != null) {
            member = memberRepository.findById(departmentDTO.getMemberId());
        }

        Department department = Department.builder()
                .name(departmentDTO.getName())
                .member(member.orElse(null))
                .build();

        departmentRepository.save(department);
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Transactional
    public String updateDepartment(Long id, DepartmentDTO departmentDTO) {
        Department oldDepartment = departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid department Id:" + id));

        Member newHead = memberRepository.findById(departmentDTO.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid member Id:" + departmentDTO.getMemberId()));

        Department updatedDepartment = Department.builder()
                .id(oldDepartment.getId())
                .name(departmentDTO.getName())
                .member(newHead)
                .regularSchedules(oldDepartment.getRegularSchedules())
                .makeupClasses(oldDepartment.getMakeupClasses())
                .build();

        departmentRepository.save(updatedDepartment);

        return newHead.getName() + " (" + newHead.getEmail() + ")";
    }

    @Transactional
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid department Id:" + id));

        departmentRepository.delete(department);
    }
}