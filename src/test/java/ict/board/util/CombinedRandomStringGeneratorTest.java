package ict.board.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CombinedRandomStringGeneratorTest {

    @Test
    public void 문자열_길이_검증() {
        String generatedString = CombinedRandomStringGenerator.generateRandomString();
        Assertions.assertEquals(12, generatedString.length(), "생성된 문자열의 길이는 12자여야 합니다.");
    }

    @Test
    public void 숫자_포함_검증() {
        String generatedString = CombinedRandomStringGenerator.generateRandomString();
        long numberCount = countMatches(generatedString, "[0-9]");
        Assertions.assertTrue(numberCount >= 2, "문자열에는 최소 두 개의 숫자가 포함되어야 합니다.");
    }

    @Test
    public void 특수문자_포함_검증() {
        String generatedString = CombinedRandomStringGenerator.generateRandomString();
        long specialCharCount = countMatches(generatedString, "[!@#$%^&*()_+\\-\\[\\]{}|;':,.<>/?]");
        Assertions.assertTrue(specialCharCount >= 2, "문자열에는 최소 두 개의 특수 문자가 포함되어야 합니다.");
    }

    @Test
    public void 대문자_포함_검증() {
        String generatedString = CombinedRandomStringGenerator.generateRandomString();
        long upperCaseCount = countMatches(generatedString, "[A-Z]");
        Assertions.assertTrue(upperCaseCount >= 1, "문자열에는 최소 한 개의 대문자가 포함되어야 합니다.");
    }

    @Test
    public void 모든_유형_문자_포함_검증() {
        String generatedString = CombinedRandomStringGenerator.generateRandomString();
        long numberCount = countMatches(generatedString, "[0-9]");
        long specialCharCount = countMatches(generatedString, "[!@#$%^&*()_+\\-\\[\\]{}|;':,.<>/?]");
        long upperCaseCount = countMatches(generatedString, "[A-Z]");
        long lowerCaseCount = countMatches(generatedString, "[a-z]");

        Assertions.assertEquals(12, numberCount + specialCharCount + upperCaseCount + lowerCaseCount,
                "모든 문자 유형이 올바르게 포함되었는지 확인합니다.");
    }

    private long countMatches(String input, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(input);
        return matcher.results().count();
    }
}