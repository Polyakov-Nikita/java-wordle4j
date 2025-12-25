package ru.yandex.practicum.log;

public interface Loggable {
    void addLogger(Logger logger);

    String getSenderName();
}
