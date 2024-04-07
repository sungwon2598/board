package ict.board.util;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class CombinedRandomStringGenerator {

    private static final String UPPER_CASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER_CASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "!@#$%^&*()_+-=[]{}|;':,.<>/?";
    private static final int LENGTH = 12;

    public static String generateRandomString() {
        SecureRandom random = new SecureRandom();
        List<Character> chars = new ArrayList<>();

        addInitialUpperCaseLetter(chars, UPPER_CASE_LETTERS, random);
        addRandomCharsToList(chars, NUMBERS, 2, random);
        addRandomCharsToList(chars, SPECIAL_CHARACTERS, 2, random);
        addRandomCharsToList(chars, UPPER_CASE_LETTERS + LOWER_CASE_LETTERS + NUMBERS + SPECIAL_CHARACTERS, LENGTH - 5, random);

        Collections.shuffle(chars, random);

        return createStringFromList(chars);
    }

    private static void addInitialUpperCaseLetter(List<Character> list, String characters, SecureRandom random) {
        list.add(characters.charAt(random.nextInt(characters.length())));
    }

    private static void addRandomCharsToList(List<Character> list, String characters, int count, SecureRandom random) {
        IntStream.range(0, count).forEach(i -> list.add(characters.charAt(random.nextInt(characters.length()))));
    }

    private static String createStringFromList(List<Character> list) {
        StringBuilder sb = new StringBuilder();
        list.forEach(sb::append);
        return sb.toString();
    }
}
