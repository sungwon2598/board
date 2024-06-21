package ict.board.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IctStaffMemberForm {

    @Email(message = "이메일 형식이 유효하지 않습니다.")
    @NotEmpty(message = "회원 이메일(ID)는 필수 입니다.")
    private String email;

    @NotEmpty(message = "이름은 필수 입니다.")
    private String name;

    @NotEmpty(message = "사번/학번은 필수 입니다.")
    private String memberNumber;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "비밀번호는 최소 8자 이상이며, 최소 하나의 문자, 하나의 숫자 및 하나의 특수 문자를 포함해야 합니다.")

    private String password;

    @NotEmpty(message = "인증 번호는 필수 입력 값입니다.")
    private String verificationCode;

    @NotEmpty(message = "주/야간은 필수 입력 값입니다.")
    private String dayShift;

}