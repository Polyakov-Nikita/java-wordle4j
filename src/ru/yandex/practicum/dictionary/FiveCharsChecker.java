package ru.yandex.practicum.dictionary;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FiveCharsChecker implements WordsChecker {
    public static final int CORRECT_WORD_LENGTH = 5;

    private static final Set<Character> notSupportedSymbols = new HashSet<>(List.of(' ', '-'));

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
