package ru.yandex.practicum.game.exceptions;

public class WordRepeatingException extends RuntimeException {
    public WordRepeatingException() {
        super("Слово уже было введено.");
    }
}
