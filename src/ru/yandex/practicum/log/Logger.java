package ru.yandex.practicum.log;

import java.io.IOException;

public interface Logger {
    void log(Loggable sender, String message) throws IOException;
}
