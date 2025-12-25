package ru.yandex.practicum.log;

import ru.yandex.practicum.exceptions.CustomIOException;
import ru.yandex.practicum.log.exceptions.LogFileNotFoundException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;

public class FileLogger implements Logger {
    public static final String FILE_EXTENSION = "log";

    private final String fileName;
    private Path file;

    public FileLogger(String fileName) {
        this.fileName = fileName + "." + FILE_EXTENSION;
    }

    @Override
    public void log(Loggable sender, String message) {
        if (file == null) {
            throw new LogFileNotFoundException(this);
        }
        writeMessage(createMessage(sender, message));
    }

    private String createMessage(Loggable sender, String message) {
        LocalTime time = LocalTime.now();
        return String.format("<%-25s> [%02d:%02d:%02d]: %s%n",
                sender.getSenderName(), time.getHour(), time.getMinute(), time.getSecond(), message);
    }

    private void writeMessage(String message) {
        try (Writer logWriter = new FileWriter(file.toFile(), true)) {
            logWriter.write(message);
        } catch (IOException e) {
            throw new CustomIOException(e);
        }
    }

    public String getFileName() {
        return fileName;
    }

    public void create() {
        try {
            file = Paths.get(fileName);
            Files.deleteIfExists(file);
            Files.createFile(file);
        } catch (IOException e) {
            throw new CustomIOException(e);
        }
    }
}
