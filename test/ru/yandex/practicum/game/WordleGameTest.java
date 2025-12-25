package ru.yandex.practicum.game;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.Tests;
import ru.yandex.practicum.dictionary.FiveCharsChecker;
import ru.yandex.practicum.dictionary.UpperENormalizer;
import ru.yandex.practicum.dictionary.WordleDictionary;
import ru.yandex.practicum.dictionary.exceptions.WordIsAbsentException;
import ru.yandex.practicum.game.exceptions.GameOverException;
import ru.yandex.practicum.game.exceptions.IncorrectWordException;
import ru.yandex.practicum.game.exceptions.VictoryException;
import ru.yandex.practicum.game.exceptions.WordRepeatingException;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class WordleGameTest {
    private static final String ANSWER = "герой";
    private static final String WORD_1 = "гонец";
    private static final String WORD_1_NEED_TO_NORMALIZE = "Гонец";
    private static final String ANALYSED_1 = "+^-^-";
    private static final String WORD_2 = "ветер";
    private static final String WORD_3 = "трава";
    private static final String WORD_4 = "книга";
    private static final String WORD_5 = "камыш";
    private static final String WORD_6 = "парус";
    private static final String ANSWER_WITH_EQUAL_LETTERS = "отток";
    private static final String EQUAL_LETTERS_WORD = "аттик";
    private static final String EQUAL_LETTERS_ANALYSED = "-++-+";
    private static final Set<String> OTHER_WORDS = new HashSet<>(List.of(
            WORD_1,
            WORD_2,
            WORD_3,
            WORD_4,
            WORD_5,
            WORD_6
    ));
    private static final Set<String> ALL_WORDS = new HashSet<>(OTHER_WORDS) {{
        add(ANSWER);
    }};
    private static final String INCORRECT_WORD = "world";


    @Test
    public void start_createAnswer() throws IOException {
        WordleDictionary dictionary = createDictionaryWithAnswer(ANSWER);
        WordleGame game = createGame(dictionary);
        game.start();
        assertEquals(ANSWER, game.getAnswer());
    }

    private static WordleDictionary createDictionaryWithAnswer(String answer) {
        WordleDictionary answerDictionary = new WordleDictionary(new FiveCharsChecker(), new UpperENormalizer(), 0);
        answerDictionary.addLogger(Tests.createLogger());
        answerDictionary.add(answer);
        return answerDictionary;
    }

    private WordleGame createGame(WordleDictionary dictionary) {
        WordleGame game = new WordleGame(dictionary);
        game.addLogger(Tests.createLogger());
        return game;
    }

    @Test
    public void start_SetStepsToOne() throws IOException {
        WordleDictionary dictionary = createDictionaryWithAnswer(ANSWER);
        WordleGame game = createGame(dictionary);
        game.start();
        assertEquals(1, game.getSteps());
    }

    @Test
    public void analyseWord() throws IOException {
        WordleDictionary dictionary = createDictionaryWithAnswer(ANSWER);
        WordleGame game = createGame(dictionary);
        game.start();
        addOtherWord(dictionary, WORD_1);
        assertEquals(ANALYSED_1, game.analyseWord(WORD_1));
    }

    @Test
    public void analyseWord_NeedToNormalize() throws IOException {
        WordleDictionary dictionary = createDictionaryWithAnswer(ANSWER);
        WordleGame game = createGame(dictionary);
        game.start();
        addOtherWord(dictionary, WORD_1);
        assertEquals(ANALYSED_1, game.analyseWord(WORD_1_NEED_TO_NORMALIZE));
    }

    @Test
    public void analyseWord_IncorrectWord() throws IOException {
        WordleDictionary dictionary = createDictionaryWithAnswer(ANSWER);
        WordleGame game = createGame(dictionary);
        game.start();
        assertThrows(IncorrectWordException.class, () -> game.analyseWord(INCORRECT_WORD));
    }

    @Test
    public void analyseWord_IncorrectWord_Word() throws IOException {
        WordleDictionary dictionary = createDictionaryWithAnswer(ANSWER);
        WordleGame game = createGame(dictionary);
        game.start();
        try {
            game.analyseWord(INCORRECT_WORD);
        } catch (IncorrectWordException e) {
            assertEquals(INCORRECT_WORD, e.getWord());
        }
    }

    private void addOtherWord(WordleDictionary dictionary, String otherWord) {
        dictionary.add(otherWord);
    }

    @Test
    public void analyseWord_WordWithEqualLetters() throws IOException {
        WordleDictionary dictionary = createDictionaryWithAnswer(ANSWER_WITH_EQUAL_LETTERS);
        WordleGame game = createGame(dictionary);
        game.start();
        addOtherWord(dictionary, EQUAL_LETTERS_WORD);
        assertEquals(EQUAL_LETTERS_ANALYSED, game.analyseWord(EQUAL_LETTERS_WORD));
    }

    @Test
    public void analyseWord_WordIsAbsentInDictionary() throws IOException {
        WordleDictionary dictionary = createDictionaryWithAnswer(ANSWER);
        WordleGame game = createGame(dictionary);
        game.start();
        assertThrows(WordIsAbsentException.class, () -> game.analyseWord(WORD_1));
    }

    @Test
    public void analyseWord_WordRepeating() throws IOException {
        WordleDictionary dictionary = createDictionaryWithAnswer(ANSWER);
        WordleGame game = createGame(dictionary);
        game.start();
        addOtherWord(dictionary, WORD_1);
        game.analyseWord(WORD_1);
        assertThrows(WordRepeatingException.class, () -> game.analyseWord(WORD_1));
    }

    @Test
    public void analyseWord_StepsIncreasing_Increase_CorrectAttempt() throws IOException {
        WordleDictionary dictionary = createDictionaryWithAnswer(ANSWER);
        WordleGame game = createGame(dictionary);
        game.start();
        addOtherWord(dictionary, WORD_1);
        game.analyseWord(WORD_1);
        assertEquals(2, game.getSteps());
    }

    @Test
    public void analyseWord_StepsIncreasing_NotIncrease_IncorrectAttempt() throws IOException {
        WordleDictionary dictionary = createDictionaryWithAnswer(ANSWER);
        WordleGame game = createGame(dictionary);
        game.start();
        assertThrows(WordIsAbsentException.class, () -> game.analyseWord(WORD_1));
        assertEquals(1, game.getSteps());
    }

    @Test
    public void analyseWord_StepsIncreasing_NotIncrease_RepeatingWord() throws IOException {
        WordleDictionary dictionary = createDictionaryWithAnswer(ANSWER);
        WordleGame game = createGame(dictionary);
        game.start();
        addOtherWord(dictionary, WORD_1);
        game.analyseWord(WORD_1);
        assertThrows(WordRepeatingException.class, () -> game.analyseWord(WORD_1));
        assertEquals(2, game.getSteps());
    }

    @Test
    public void analyseWord_GameOver() throws IOException {
        WordleDictionary dictionary = createDictionaryWithAnswer(ANSWER);
        WordleGame game = createGame(dictionary);
        game.start();
        addOtherWords(dictionary);
        game.analyseWord(WORD_1);
        game.analyseWord(WORD_2);
        game.analyseWord(WORD_3);
        game.analyseWord(WORD_4);
        game.analyseWord(WORD_5);
        assertThrows(GameOverException.class, () -> game.analyseWord(WORD_6));
    }

    private void addOtherWords(WordleDictionary dictionary) {
        for (String word : OTHER_WORDS) {
            dictionary.add(word);
        }
    }

    @Test
    public void analyseWord_Victory() throws IOException {
        WordleDictionary dictionary = createDictionaryWithAnswer(ANSWER);
        WordleGame game = createGame(dictionary);
        game.start();
        addOtherWords(dictionary);
        game.analyseWord(WORD_1);
        game.analyseWord(WORD_2);
        assertThrows(VictoryException.class, () -> game.analyseWord(ANSWER));
    }

    @Test
    public void analyseWord_Victory_Step() throws IOException {
        WordleDictionary dictionary = createDictionaryWithAnswer(ANSWER);
        WordleGame game = createGame(dictionary);
        game.start();
        addOtherWords(dictionary);
        game.analyseWord(WORD_1);
        game.analyseWord(WORD_2);
        try {
            game.analyseWord(ANSWER);
        } catch (VictoryException e) {
            assertEquals(3, e.getStep());
        }
    }

    @Test
    public void analyseWord_Victory_FirstTurn() throws IOException {
        WordleDictionary dictionary = createDictionaryWithAnswer(ANSWER);
        WordleGame game = createGame(dictionary);
        game.start();
        addOtherWords(dictionary);
        assertThrows(VictoryException.class, () -> game.analyseWord(ANSWER));
    }

    @Test
    public void analyseWord_Victory_LastTurn() throws IOException {
        WordleDictionary dictionary = createDictionaryWithAnswer(ANSWER);
        WordleGame game = createGame(dictionary);
        game.start();
        addOtherWords(dictionary);
        game.analyseWord(WORD_1);
        game.analyseWord(WORD_2);
        game.analyseWord(WORD_3);
        game.analyseWord(WORD_4);
        game.analyseWord(WORD_5);
        assertThrows(VictoryException.class, () -> game.analyseWord(ANSWER));
    }

    @Test
    public void giveHint() throws IOException {
        WordleDictionary dictionary = createDictionaryWithAnswer(ANSWER);
        WordleGame game = createGame(dictionary);
        game.start();
        addOtherWords(dictionary);
        assertTrue(ALL_WORDS.contains(game.giveHint()));
    }
}
