package ict.board.dto;


import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardForm {

    @NotEmpty(message = "제목을 입력해주세요")
    private String title;

    @NotEmpty(message = "내용을 입력해주세요")
    private String content;

    private boolean requesterSameWriter;

    private String requester;

    private String requesterLocation;
}