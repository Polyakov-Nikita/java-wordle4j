package ru.yandex.practicum.game.exceptions;

public class VictoryException extends RuntimeException {
    private final int step;

    public VictoryException(int step) {
        super(String.format("Слово отгадано на %dм ходу. Победа.", step));
        this.step = step;
    }

    public int getStep() {
        return step;
    }
}
