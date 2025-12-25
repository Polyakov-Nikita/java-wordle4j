package ru.yandex.practicum.game.exceptions;

public class IncorrectWordException extends RuntimeException {
    private final String word;

    public IncorrectWordException(String word) {
        super(String.format("Слово '%s' некорректно.", word));
        this.word = word;
    }

    public String getWord() {
        return word;
    }
}
