package ru.yandex.practicum.log;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.log.exceptions.LogFileNotFoundException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileLoggerTest implements Loggable {
    private static final String LOG_FILE_NAME = "log-test";
    private static final FileLogger FILE_LOGGER = new FileLogger(LOG_FILE_NAME);
    private static final Path LOG_FILE = Paths.get(FILE_LOGGER.getFileName());
    private static final String SENDER_NAME = "LoggerTest";
    private static final String MESSAGE = "Message";

    @Override
    public void addLogger(Logger logger) {

    }

    @Override
    public String getSenderName() {
        return SENDER_NAME;
    }

    @BeforeAll
    public static void createLog() throws IOException {
        FILE_LOGGER.create();
    }

    @AfterAll
    public static void deleteLog() throws IOException {
        Files.deleteIfExists(LOG_FILE);
    }

    @Test
    public void constructor() {
        assertEquals(LOG_FILE_NAME + "." + FileLogger.FILE_EXTENSION, FILE_LOGGER.getFileName());
    }

    @Test
    public void create() throws IOException {
        Files.deleteIfExists(LOG_FILE);
        assertFalse(Files.exists(LOG_FILE));
        FILE_LOGGER.create();
        assertTrue(Files.exists(LOG_FILE));
    }

    @Test
    public void create_AlreadyExists() throws IOException {
        FILE_LOGGER.create();
        writeLine();
        String loggedString = readLine();
        FILE_LOGGER.create();
        assertNotNull(loggedString);
        assertNull(readLine());
    }

    private void writeLine() throws IOException {
        Writer logWriter = new FileWriter(FILE_LOGGER.getFileName());
        logWriter.write("12345");
        logWriter.close();
    }

    private String readLine() throws IOException {
        BufferedReader logReader = new BufferedReader(new FileReader(FILE_LOGGER.getFileName()));
        String loggedString = logReader.readLine();
        logReader.close();
        return loggedString;
    }

    @Test
    public void log() throws IOException {
        logMessage();
        assertNotNull(readLine());
    }

    private void logMessage() throws IOException {
        FILE_LOGGER.log(this, MESSAGE);
    }

    @Test
    public void log_NotCreated() {
        FileLogger withoutFile = new FileLogger("");
        assertThrows(LogFileNotFoundException.class, () -> withoutFile.log(this, ""));
    }

    @Test
    public void log_MultipleMessages() throws IOException {
        logMessage();
        logMessage();
        logMessage();
        assertEquals(3, getMessagesCount());
    }

    private int getMessagesCount() throws IOException {
        return readAllLines().size();
    }

    private List<String> readAllLines() throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        BufferedReader logReader = new BufferedReader(new FileReader(FILE_LOGGER.getFileName()));
        while (logReader.ready()) {
            lines.add(logReader.readLine());
        }
        logReader.close();
        return lines;
    }

    @Test
    public void log_CorrectData() throws IOException {
        logMessage();
        LocalTime now = LocalTime.now();
        assertEquals(String.format("<%-25s> [%02d:%02d:%02d]: %s",
                SENDER_NAME, now.getHour(), now.getMinute(), now.getSecond(), MESSAGE), readLine());
    }
}
