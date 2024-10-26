package ict.board.controller.schedule;

import ict.board.domain.schedule.Category;
import ict.board.domain.schedule.Software;
import ict.board.dto.SoftwareResponseDto;
import ict.board.dto.request.SoftwareDto;
import ict.board.service.CategoryService;
import ict.board.service.SoftwareService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/software")
@RequiredArgsConstructor
public class SoftwareController {
    private final SoftwareService softwareService;
    private final CategoryService categoryService;

    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    @GetMapping("/list")
    public String listSoftware(Model model) {
        List<Software> allSoftware = softwareService.getAllSoftware();
        List<SoftwareResponseDto> softwareDtos = allSoftware.stream()
                .map(software -> new SoftwareResponseDto(
                        software.getId(),
                        software.getName(),
                        software.getVersion(),
                        software.getCategory() != null ? software.getCategory().getName() : "No Category"))
                .collect(Collectors.toList());
        model.addAttribute("softwares", softwareDtos);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "software-list";
    }

    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("softwareDto", new SoftwareDto());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "software-add";
    }

    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    @PostMapping("/add")
    public String addSoftware(@Valid @ModelAttribute SoftwareDto softwareDto,
                              BindingResult bindingResult,
                              Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "software-add";
        }
        softwareService.addSoftware(softwareDto);
        return "redirect:/software/list";
    }

    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    @PostMapping("/delete/{id}")
    public String deleteSoftware(@PathVariable Long id) {
        softwareService.deleteSoftware(id);
        return "redirect:/software/list";
    }

    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    @GetMapping("/categories")
    @ResponseBody
    public List<Category> getCategories() {
        return categoryService.getAllCategories();
    }

    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    @PutMapping("/update/{id}")
    @ResponseBody
    public ResponseEntity<?> updateSoftware(@PathVariable Long id, @RequestBody SoftwareDto softwareDto) {
        try {
            Software updatedSoftware = softwareService.updateSoftware(id, softwareDto);
            SoftwareResponseDto responseDto = new SoftwareResponseDto(
                    updatedSoftware.getId(),
                    updatedSoftware.getName(),
                    updatedSoftware.getVersion(),
                    updatedSoftware.getCategory() != null ? updatedSoftware.getCategory().getName() : "No Category");
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}