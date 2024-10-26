package ict.board.controller.schedule;

import ict.board.domain.member.Member;
import ict.board.dto.request.DepartmentDTO;
import ict.board.service.DepartmentService;
import ict.board.service.MemberService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/department")
@PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
@RequiredArgsConstructor
@Slf4j
public class DepartmentController {

    private final DepartmentService departmentService;
    private final MemberService memberService;

    @GetMapping("/register")
    public String register(Model model) {
        List<Member> members = memberService.getAllMembers();
        model.addAttribute("members", members);
        model.addAttribute("departmentDTO", new DepartmentDTO());
        return "department/register";
    }

    @PostMapping("/register")
    public String registerDepartment(@ModelAttribute DepartmentDTO departmentDTO) {
        departmentService.createDepartment(departmentDTO);
        return "redirect:/department/list";
    }

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("members", memberService.getAllMembers());
        return "department/list";
    }

    @PostMapping("/update/{id}")
    @ResponseBody
    public ResponseEntity<?> updateDepartment(@PathVariable Long id, @RequestBody DepartmentDTO departmentDTO) {
        String headName = departmentService.updateDepartment(id, departmentDTO);
        return ResponseEntity.ok(Map.of("headName", headName));
    }

    @PostMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok().build();
    }
}