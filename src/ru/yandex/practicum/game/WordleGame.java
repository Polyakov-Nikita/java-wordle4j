package ru.yandex.practicum.game;

import ru.yandex.practicum.dictionary.WordleDictionary;
import ru.yandex.practicum.dictionary.exceptions.WordIsAbsentException;
import ru.yandex.practicum.game.exceptions.GameOverException;
import ru.yandex.practicum.game.exceptions.IncorrectWordException;
import ru.yandex.practicum.game.exceptions.VictoryException;
import ru.yandex.practicum.game.exceptions.WordRepeatingException;
import ru.yandex.practicum.log.Loggable;
import ru.yandex.practicum.log.Logger;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/*
в этом классе хранится словарь и состояние игры
    текущий шаг
    всё что пользователь вводил
    правильный ответ

в этом классе нужны методы, которые
    проанализируют совпадение слова с ответом
    предложат слово-подсказку с учётом всего, что вводил пользователь ранее

не забудьте про специальные типы исключений для игровых и неигровых ошибок
 */
public class WordleGame implements Loggable {
    private static final int ATTEMPTS_COUNT = 6;
    private static final char ABSENT = '-';
    private static final char RIGHT_POSITION = '+';
    private static final char OTHER_POSITION = '^';

    private final WordleDictionary dictionary;
    private final Set<Character> forbiddenLetters;
    private final Set<Character> requiredLetters;

    private String answer;
    private int steps;
    private Logger logger;

    public WordleGame(WordleDictionary dictionary) {
        this.dictionary = dictionary;
        forbiddenLetters = new HashSet<>();
        requiredLetters = new HashSet<>();
    }

    @Override
    public void addLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public String getSenderName() {
        return "WordleGame";
    }

    public String getAnswer() {
        return answer;
    }

    private void setSteps(int steps) throws IOException {
        this.steps = steps;
        logger.log(this, String.format("Количество шагов теперь равно '%d'.", steps));
    }

    public int getSteps() {
        return steps;
    }

    public void start() throws IOException {
        logger.log(this, "Игра началась.");
        answer = dictionary.getRandomWord();
        logger.log(this, String.format("Задумано слово '%s'.", answer));
        setSteps(1);
    }

    public String analyseWord(String word)
            throws IOException, WordIsAbsentException, WordRepeatingException, GameOverException {
        word = dictionary.normalizeWord(word);
        checkCorrectness(word);
        logger.log(this, String.format("Получено слово '%s'.", word));
        checkVictory(word);
        checkContains(word);
        checkExcluded(word);
        setSteps(steps + 1);
        checkGameOver();
        dictionary.excludeWord(word);
        String analysis = analyseWordLetters(word);
        logger.log(this, String.format("Анализ слова: %s.", analysis));
        updateDictionary(word, analysis);
        return analysis;
    }

    private void checkCorrectness(String word) throws IOException, IncorrectWordException {
        if (!dictionary.checkWord(word)) {
            logger.log(this, "Слово %s некорректно.");
            throw new IncorrectWordException(word);
        }
    }

    private void checkVictory(String word) throws IOException {
        if (answer.equals(word)) {
            logger.log(this, "Слово совпало с загаданным.");
            throw new VictoryException(steps);
        }
    }

    private void checkContains(String word) throws IOException, WordIsAbsentException {
        if (!dictionary.containsWord(word)) {
            logger.log(this, "Слово не найдено.");
            throw new WordIsAbsentException();
        }
    }

    private void checkExcluded(String word) throws IOException, WordRepeatingException {
        if (dictionary.isExcludedWord(word)) {
            logger.log(this, "Такое слово уже было.");
            throw new WordRepeatingException();
        }
    }

    private void checkGameOver() throws IOException, GameOverException {
        if (steps > ATTEMPTS_COUNT) {
            logger.log(this, "Попытки закончились.");
            throw new GameOverException();
        }
    }

    private String analyseWordLetters(String word) {
        StringBuilder analysis = new StringBuilder();
        int wordLength = word.length();
        for (int i = 0; i < wordLength; i++) {
            analysis.append(analyseLetter(word, i));
        }
        return analysis.toString();
    }

    private char analyseLetter(String word, int letterIndex) {
        char letter = word.charAt(letterIndex);
        if (answer.charAt(letterIndex) == letter) {
            return RIGHT_POSITION;
        }
        if (answer.indexOf(letter) == -1) {
            return ABSENT;
        }
        return OTHER_POSITION;
    }

    private void updateDictionary(String word, String analysis) throws IOException {
        int wordLength = word.length();
        Set<Character> absentLetters = new HashSet<>();
        Set<Character> rightLetters = new HashSet<>();
        for (int i = 0; i < wordLength; i++) {
            if (analysis.charAt(i) == ABSENT) {
                absentLetters.add(word.charAt(i));
            } else {
                rightLetters.add(word.charAt(i));
            }
        }
        removeWithAbsent(absentLetters);
        removeWithoutRight(rightLetters);
    }

    private void removeWithAbsent(Set<Character> absentLetters) throws IOException {
        absentLetters.removeAll(forbiddenLetters);
        if (!absentLetters.isEmpty()) {
            dictionary.removeWithLetters(absentLetters);
            forbiddenLetters.addAll(absentLetters);
        }
    }

    private void removeWithoutRight(Set<Character> rightLetters) throws IOException {
        rightLetters.removeAll(requiredLetters);
        if (!rightLetters.isEmpty()) {
            dictionary.removeWithoutLetters(rightLetters);
            requiredLetters.addAll(rightLetters);
        }
    }

    public String giveHint() throws IOException {
        String hint = dictionary.getRandomWord();
        logger.log(this, String.format("Дана подсказка: '%s'.", hint));
        return hint;
    }
}
