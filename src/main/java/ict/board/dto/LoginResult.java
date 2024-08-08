package ict.board.dto;

import ict.board.domain.member.Member;
import lombok.Getter;

@Getter
public class LoginResult {
    private final boolean success;
    private final String errorMessage;
    private final Member member;

    private LoginResult(boolean success, String errorMessage, Member member) {
        this.success = success;
        this.errorMessage = errorMessage;
        this.member = member;
    }

    public static LoginResult success(Member member) {
        return new LoginResult(true, null, member);
    }

    public static LoginResult fail(String errorMessage) {
        return new LoginResult(false, errorMessage, null);
    }
}