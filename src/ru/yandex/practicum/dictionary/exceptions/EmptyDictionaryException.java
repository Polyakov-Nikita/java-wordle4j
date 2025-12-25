package ru.yandex.practicum.dictionary.exceptions;

public class EmptyDictionaryException extends RuntimeException {
    public EmptyDictionaryException() {
        super("Словарь пуст. Используйте add() для добавления слов.");
    }
}
