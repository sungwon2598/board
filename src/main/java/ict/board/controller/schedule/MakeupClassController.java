package ict.board.controller.schedule;

import ict.board.domain.schedule.MakeupClass;
import ict.board.dto.request.RegisterMakeupClassDto;
import ict.board.service.MakeupClassService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequestMapping("/makeup-classes")
@RequiredArgsConstructor
public class MakeupClassController {

    private final MakeupClassService makeupClassService;

    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerMakeupClassDto", new RegisterMakeupClassDto());
        addCommonAttributes(model);
        return "makeup-classes/register";
    }

    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    @PostMapping("/register")
    public String registerMakeupClass(@Valid @ModelAttribute RegisterMakeupClassDto dto, BindingResult bindingResult,
                                      Model model) {
        if (bindingResult.hasErrors()) {
            addCommonAttributes(model);
            return "makeup-classes/register";
        }

        try {
            makeupClassService.registerMakeupClass(dto);
            return "redirect:/makeup-classes?date=" + dto.getDate();
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("classroomName", "error.classroomName", e.getMessage());
            addCommonAttributes(model);
            return "makeup-classes/register";
        }
    }

    @GetMapping
    public String listMakeupClasses(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {
        if (date == null) {
            date = LocalDate.now(); // 날짜가 제공되지 않으면 오늘 날짜 사용
        }
        List<MakeupClass> makeupClasses = makeupClassService.getMakeupClassesByDate(date);
        model.addAttribute("makeupClasses", makeupClasses);
        model.addAttribute("selectedDate", date);
        return "makeup-classes/list";
    }


    @GetMapping("/all")
    public String listAllMakeupClasses(Model model) {
        List<MakeupClass> makeupClasses = makeupClassService.getAllMakeupClasses();
        model.addAttribute("makeupClasses", makeupClasses);
        return "makeup-classes/list-all";
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<MakeupClass> getMakeupClass(@PathVariable Long id) {
        MakeupClass makeupClass = makeupClassService.getMakeupClassById(id);
        return ResponseEntity.ok(makeupClass);
    }

    @PostMapping("/edit/{id}")
    @ResponseBody
    public ResponseEntity<?> editMakeupClass(@PathVariable Long id, @RequestBody RegisterMakeupClassDto dto) {
        try {
            MakeupClass updatedClass = makeupClassService.updateMakeupClass(id, dto);
            return ResponseEntity.ok(updatedClass);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/classrooms")
    @ResponseBody
    public ResponseEntity<List<String>> getAvailableClassrooms() {
        List<String> classrooms = makeupClassService.getAvailableClassrooms();
        return ResponseEntity.ok(classrooms);
    }

    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    @PostMapping("/delete/{id}")
    public String deleteMakeupClass(@PathVariable Long id) {
        makeupClassService.deleteMakeupClass(id);
        return "redirect:/makeup-classes/all";
    }

    private void addCommonAttributes(Model model) {
        model.addAttribute("classrooms", makeupClassService.getAvailableClassrooms());
    }
}