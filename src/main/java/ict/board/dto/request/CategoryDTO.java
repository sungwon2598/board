package ict.board.dto.request;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
public class CategoryDTO {
    @NotBlank(message = "Category name is required")
    private String name;
}