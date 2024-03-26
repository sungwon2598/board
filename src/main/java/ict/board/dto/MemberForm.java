package ict.board.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberForm {

    @NotEmpty(message = "회원 이메일(ID)는 필수 입니다.")
    private String email;

    private String name;
    private String team;
    private String building;
    private String memberNumber;
    private String password;
}
