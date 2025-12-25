package ru.yandex.practicum.dictionary.exceptions;

public class EmptyAllowedWordsException extends RuntimeException {
    public EmptyAllowedWordsException() {
        super("Доступных слов не осталось.");
    }
}
