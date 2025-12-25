package ru.yandex.practicum.log.exceptions;

import ru.yandex.practicum.log.FileLogger;

public class LogFileNotFoundException extends RuntimeException {
    public LogFileNotFoundException(FileLogger fileLogger) {
        super(String.format("Лог-файл <%s> не найден. Используйте create() для создания.", fileLogger.getFileName()));
    }
}
