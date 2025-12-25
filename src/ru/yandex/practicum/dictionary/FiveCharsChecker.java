package ru.yandex.practicum.dictionary;

public class FiveCharsChecker implements WordsChecker {
    public static final int CORRECT_WORD_LENGTH = 5;

    @Override
    public boolean checkWord(String word) {
        if (word.length() != CORRECT_WORD_LENGTH) {
            return false;
        }
        return !checkNotSupportedSymbols(word);
    }

    private boolean checkNotSupportedSymbols(String word) {
        for (char symbol : word.toCharArray()) {
            if ((symbol < 'а' || symbol > 'я') && symbol != 'ё') {
                return true;
            }
        }
        return false;
    }
}
