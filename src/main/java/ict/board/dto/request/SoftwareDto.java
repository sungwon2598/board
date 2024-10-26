package ict.board.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SoftwareDto {
    @NotBlank(message = "소프트웨어 이름은 필수입니다.")
    private String name;

    @NotBlank(message = "버전은 필수입니다.")
    private String version;

    @NotNull(message = "카테고리를 선택해주세요.")
    private Long categoryId;
}
