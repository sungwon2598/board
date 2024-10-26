package ict.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SoftwareResponseDto {
    private Long id;
    private String name;
    private String version;
    private String categoryName;
}