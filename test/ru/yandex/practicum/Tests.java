package ru.yandex.practicum;

import ru.yandex.practicum.log.Loggable;
import ru.yandex.practicum.log.Logger;

public class Tests {
    public static Logger createLogger() {
        return new Logger() {
            @Override
            public void log(Loggable sender, String message) {

            }

        };
    }
}
