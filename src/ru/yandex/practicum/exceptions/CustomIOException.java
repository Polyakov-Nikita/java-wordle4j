package ru.yandex.practicum.exceptions;

import java.io.IOException;

public class CustomIOException extends RuntimeException {
    public CustomIOException(IOException e) {
        super(e.getMessage());
    }
}
