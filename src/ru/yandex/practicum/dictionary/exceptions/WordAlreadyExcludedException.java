package ru.yandex.practicum.dictionary.exceptions;

public class WordAlreadyExcludedException extends RuntimeException {
    private final String word;

    public WordAlreadyExcludedException(String word) {
        super(String.format("Слово <%s> уже исключено.", word));
        this.word = word;
    }

    public String getWord() {
        return word;
    }
}
