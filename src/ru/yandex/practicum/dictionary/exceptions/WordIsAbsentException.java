package ru.yandex.practicum.dictionary.exceptions;

public class WordIsAbsentException extends RuntimeException {
    public WordIsAbsentException() {
        super("В словаре такого слова нет.");
    }
}
