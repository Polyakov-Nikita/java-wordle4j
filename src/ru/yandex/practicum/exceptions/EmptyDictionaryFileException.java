package ru.yandex.practicum.exceptions;

public class EmptyDictionaryFileException extends RuntimeException {
    public EmptyDictionaryFileException() {
        super("Файл словаря пуст.");
    }
}
