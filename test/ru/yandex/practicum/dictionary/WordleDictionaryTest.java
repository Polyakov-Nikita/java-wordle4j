package ru.yandex.practicum.dictionary;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.Tests;
import ru.yandex.practicum.dictionary.exceptions.EmptyAllowedWordsException;
import ru.yandex.practicum.dictionary.exceptions.EmptyDictionaryException;
import ru.yandex.practicum.dictionary.exceptions.WordAlreadyExcludedException;
import ru.yandex.practicum.dictionary.exceptions.WordIsAbsentException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class WordleDictionaryTest {
    private static final String WORD_1 = "мазок";
    private static final String WORD_2 = "замок";
    private static final String WORD_3 = "книга";
    private static final String NOT_CORRECT_WORD = "привет";
    private static final String WORD_TO_NORMALIZE = "Арбуз";
    private static final String NORMALIZED_WORD = "арбуз";
    private static final int SEED = 0;
    private static final Set<String> ALL_WORDS = new HashSet<>(List.of(WORD_1, WORD_2, WORD_3));
    private static final String ABSENT_WORD = "трель";
    private static final Set<Character> LETTERS_FROM_WORD_3 = new HashSet<>(List.of('н', 'и'));
    private static final String INCORRECT_WORD = "world";

    private WordleDictionary dictionary;

    @BeforeEach
    public void createDictionary() {
        dictionary = new WordleDictionary(new FiveCharsChecker(), new UpperENormalizer(), SEED);
        dictionary.addLogger(Tests.createLogger());
    }

    @Test
    public void constructor() {
        dictionary = new WordleDictionary(new FiveCharsChecker(), new UpperENormalizer(), SEED);
        dictionary.addLogger(null);
        assertEquals("WordleDictionary", dictionary.getSenderName());
    }

    @Test
    public void add() {
        dictionary.add(WORD_1);
        assertTrue(dictionary.containsWord(WORD_1));
    }

    @Test
    public void add_MultipleWords() {
        dictionary.add(WORD_1);
        dictionary.add(WORD_2);
        assertTrue(dictionary.containsWord(WORD_1));
        assertTrue(dictionary.containsWord(WORD_2));
    }

    @Test
    public void add_NotCorrectWord() {
        dictionary.add(NOT_CORRECT_WORD);
        assertFalse(dictionary.containsWord(NOT_CORRECT_WORD));
    }

    @Test
    public void add_Normalizing() {
        dictionary.add(WORD_TO_NORMALIZE);
        assertFalse(dictionary.containsWord(WORD_TO_NORMALIZE));
        assertTrue(dictionary.containsWord(NORMALIZED_WORD));
    }

    @Test
    public void getRandomWord() {
        addAllWords();
        assertTrue(ALL_WORDS.contains(dictionary.getRandomWord()));
    }

    @Test
    public void getRandomWord_EmptyAllowedWords() {
        addAllWords();
        int allWordsCount = ALL_WORDS.size();
        for (int i = 0; i < allWordsCount; i++) {
            dictionary.excludeWord(dictionary.getRandomWord());
        }
        assertThrows(EmptyAllowedWordsException.class, () -> dictionary.getRandomWord());
    }

    private void addAllWords() {
        for (String word : ALL_WORDS) {
            dictionary.add(word);
        }
    }

    @Test
    public void getRandomWord_WithoutExcluded() {
        addAllWords();
        Set<String> excludedWords = new HashSet<>(List.of(WORD_2, WORD_3));
        dictionary.excludeWord(WORD_2);
        dictionary.excludeWord(WORD_3);
        assertNotContainsAllRandoms(excludedWords);
    }

    private void assertNotContainsAllRandoms(Set<String> set) {
        for (int i = 0; i < 10; i++) {
            assertFalse(set.contains(dictionary.getRandomWord()));
        }
    }

    @Test
    public void getRandomWord_EmptyDictionary() {
        assertThrows(EmptyDictionaryException.class, () -> dictionary.getRandomWord());
    }

    @Test
    public void excludeWord() {
        addAllWords();
        dictionary.excludeWord(WORD_1);
        assertTrue(dictionary.isExcludedWord(WORD_1));
    }

    @Test
    public void excludeWord_AbsentWord() {
        addAllWords();
        assertThrows(WordIsAbsentException.class, () -> dictionary.excludeWord(ABSENT_WORD));
    }

    @Test
    public void excludeWord_ExcludedWord() {
        addAllWords();
        dictionary.excludeWord(WORD_1);
        assertThrows(WordAlreadyExcludedException.class, () -> dictionary.excludeWord(WORD_1));
    }

    @Test
    public void excludeWord_ExcludedWord_CorrectWord() {
        addAllWords();
        dictionary.excludeWord(WORD_1);
        try {
            dictionary.excludeWord(WORD_1);
        } catch (WordAlreadyExcludedException e) {
            assertEquals(WORD_1, e.getWord());
        }
    }

    @Test
    public void removeWithoutLetters() {
        addAllWords();
        Set<String> excludedWords = new HashSet<>(List.of(WORD_1, WORD_2));
        dictionary.removeWithoutLetters(LETTERS_FROM_WORD_3);
        assertNotContainsAllRandoms(excludedWords);
    }

    @Test
    public void removeWithLetters() {
        addAllWords();
        Set<String> excludedWords = new HashSet<>(List.of(WORD_3));
        dictionary.removeWithLetters(LETTERS_FROM_WORD_3);
        assertNotContainsAllRandoms(excludedWords);
    }

    @Test
    public void normalizeWord() {
        assertEquals(NORMALIZED_WORD, dictionary.normalizeWord(WORD_TO_NORMALIZE));
    }

    @Test
    public void checkWord() {
        assertTrue(dictionary.checkWord(WORD_1));
    }

    @Test
    public void checkWord_IncorrectWord() {
        assertFalse(dictionary.checkWord(INCORRECT_WORD));
    }
}
