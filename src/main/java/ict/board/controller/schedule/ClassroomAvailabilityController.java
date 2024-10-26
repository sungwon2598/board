package ict.board.controller.schedule;

import ict.board.domain.schedule.Classroom;
import ict.board.dto.ClassroomDto;
import ict.board.dto.request.ClassroomAvailabilityRequest;
import ict.board.service.ClassroomAvailabilityService;
import ict.board.service.ClassroomService;
import ict.board.service.SoftwareService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/classrooms")
@RequiredArgsConstructor
public class ClassroomAvailabilityController {

    private final ClassroomService classroomService;
    private final ClassroomAvailabilityService classroomAvailabilityService;
    private final SoftwareService softwareService;

    @GetMapping("/search")
    public String showSearchForm(Model model) {
        model.addAttribute("request", new ClassroomAvailabilityRequest());
        model.addAttribute("allSoftware", softwareService.getAllSoftware());
        return "classroom-search";
    }

    @PostMapping("/search")
    public String searchClassrooms(@Valid @ModelAttribute ClassroomAvailabilityRequest request,
                                   BindingResult bindingResult,
                                   Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allSoftware", softwareService.getAllSoftware());
            return "classroom-search";
        }

        List<Classroom> availableClassrooms = classroomAvailabilityService.findAvailableClassrooms(
                request.getSoftwareIds(), request.getDate(), request.getStartTime(), request.getEndTime());

        Set<Long> softwareIds = request.getSoftwareIds();
        log.info(String.valueOf(softwareIds.size()));

        model.addAttribute("classrooms", availableClassrooms);
        return "classroom-results";
    }

    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("classroomDto", new ClassroomDto());
        model.addAttribute("allSoftware", softwareService.getAllSoftware());
        return "register";
    }

    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    @PostMapping("/register")
    public String registerClassroom(ClassroomDto classroomDto) {
        classroomService.registerClassroom(classroomDto);
        return "redirect:/classrooms/register";
    }
}