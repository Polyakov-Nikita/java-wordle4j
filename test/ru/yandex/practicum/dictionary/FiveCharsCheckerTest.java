package ru.yandex.practicum.dictionary;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FiveCharsCheckerTest {
    private static final String CORRECT_LENGTH_WORD = "слово";
    private static final String SHORT_WORD = "арка";
    private static final String LONG_WORD = "привет";
    private static final String WORD_WITH_SPACE = "я шел";
    private static final String WORD_WITH_DASH = "ко-то";
    private static final String NOT_RUSSIAN_WORD = "world";

    private static FiveCharsChecker checker;

    @BeforeAll
    public static void createChecker() {
        checker = new FiveCharsChecker();
    }

    @Test
    public void checkWord_CorrectLength() {
        assertTrue(checker.checkWord(CORRECT_LENGTH_WORD));
    }

    @Test
    public void checkWord_ShortWord() {
        assertFalse(checker.checkWord(SHORT_WORD));
    }

    @Test
    public void checkWord_LongWord() {
        assertFalse(checker.checkWord(LONG_WORD));
    }

    @Test
    public void checkWord_NotSupportedSymbols_NotRussian() {
        assertFalse(checker.checkWord(NOT_RUSSIAN_WORD));
    }

    @Test
    public void checkWord_NotSupportedSymbols_WordWithSpaces() {
        assertFalse(checker.checkWord(WORD_WITH_SPACE));
    }

    @Test
    public void checkWord_NotSupportedSymbols_WordWithDash() {
        assertFalse(checker.checkWord(WORD_WITH_DASH));
    }
}
