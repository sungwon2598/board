package ict.board.service;

import ict.board.domain.schedule.Category;
import ict.board.domain.schedule.Software;
import ict.board.repository.CategoryRepository;
import ict.board.repository.SoftwareRepository;
import ict.board.dto.request.SoftwareDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SoftwareService {

    private final SoftwareRepository softwareRepository;
    private final CategoryRepository categoryRepository;

    public List<Software> getAllSoftware() {
        return softwareRepository.findAllByOrderByNameAsc();
    }

    @Transactional
    public Software addSoftware(SoftwareDto softwareDto) {
        Category category = categoryRepository.findById(softwareDto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));

        Software software = Software.builder()
                .name(softwareDto.getName())
                .version(softwareDto.getVersion())
                .category(category)
                .build();
        return softwareRepository.save(software);
    }

    @Transactional
    public void deleteSoftware(Long id) {
        softwareRepository.deleteById(id);
    }

    @Transactional
    public Software updateSoftware(Long id, SoftwareDto softwareDto) {
        Software software = softwareRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid software ID"));

        Category category = categoryRepository.findById(softwareDto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));

        Software updatedSoftware = software.update(softwareDto.getName(), softwareDto.getVersion(), category);
        return softwareRepository.save(updatedSoftware);
    }
}