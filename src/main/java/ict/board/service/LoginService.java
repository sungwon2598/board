package ict.board.service;

import ict.board.dto.LoginResult;
import ict.board.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;

    public LoginResult authenticate(String email, String password) {
        return memberRepository.findMemberByEmail(email)
                .map(member -> {
                    if (isPasswordValid(password, member.getPassword())) {
                        return LoginResult.success(member);
                    } else {
                        return LoginResult.fail("이메일 또는 비밀번호가 올바르지 않습니다.");
                    }
                })
                .orElse(LoginResult.fail("이메일 또는 비밀번호가 올바르지 않습니다."));
    }

    private boolean isPasswordValid(String rawPassword, String storedPassword) {
        return BCrypt.checkpw(rawPassword, storedPassword);
    }
}