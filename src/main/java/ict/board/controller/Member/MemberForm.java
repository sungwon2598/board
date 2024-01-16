package ict.board.controller.Member;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MemberForm {

    @NotEmpty(message = "회원 이메일(ID)는 필수 입니다.")
    private String email;

    private String name;
    private String city;
    private String street;
    private String zipcode;
    private String password;
}
