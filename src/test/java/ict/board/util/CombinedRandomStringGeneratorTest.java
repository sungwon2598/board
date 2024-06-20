package ict.board.util;

import ict.board.util.cache.VerificationCodeCache;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CombinedRandomStringGeneratorTest {
    private static final String testEmail = "tjddnjs2598@naver.com";

    private final CombinedRandomStringGenerator combinedRandomStringGenerator = new CombinedRandomStringGenerator();

    private final VerificationCodeCache verificationCodeCache = new VerificationCodeCache();

    @Test
    public void 문자열_길이_검증() {
        String generatedString = combinedRandomStringGenerator.generateRandomString();
        Assertions.assertEquals(12, generatedString.length(), "생성된 문자열의 길이는 12자여야 합니다.");
    }

    @Test
    public void 숫자_포함_검증() {
        String generatedString = combinedRandomStringGenerator.generateRandomString();
        long numberCount = countMatches(generatedString, "[0-9]");
        Assertions.assertTrue(numberCount >= 2, "문자열에는 최소 두 개의 숫자가 포함되어야 합니다.");
    }

    @Test
    public void 특수문자_포함_검증() {
        String generatedString = combinedRandomStringGenerator.generateRandomString();
        long specialCharCount = countMatches(generatedString, "[!@#$%^&*()_+\\-\\[\\]{}|;':,.<>/?]");
        Assertions.assertTrue(specialCharCount >= 2, "문자열에는 최소 두 개의 특수 문자가 포함되어야 합니다.");
    }

    @Test
    public void 대문자_포함_검증() {
        String generatedString = combinedRandomStringGenerator.generateRandomString();
        long upperCaseCount = countMatches(generatedString, "[A-Z]");
        Assertions.assertTrue(upperCaseCount >= 1, "문자열에는 최소 한 개의 대문자가 포함되어야 합니다.");
    }

    private long countMatches(String input, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(input);
        return matcher.results().count();
    }

    @Test
    public void 인증번호_생성_후_캐시저장_및_검증() {
        String generatedString = combinedRandomStringGenerator.generateRandomString();
        verificationCodeCache.storeCode(testEmail,generatedString);
        org.assertj.core.api.Assertions.assertThat(verificationCodeCache.isValidCode(testEmail,generatedString)).isTrue();
    }
}