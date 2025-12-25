package ru.yandex.practicum.game.exceptions;

public class GameOverException extends RuntimeException {
    public GameOverException() {
        super("Попытки закончились. Игра окончена.");
    }
}
