package ru.yandex.practicum;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.dictionary.WordleDictionary;
import ru.yandex.practicum.exceptions.EmptyDictionaryFileException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WordleDictionaryLoaderTest {
    private static final String WORDS_FILE_NAME = "WORDS.txt";
    private static final List<String> WORDS = new ArrayList<>(List.of(
            "земля",
            "замок",
            "хомяк"));
    private static final String EMPTY_FILE_NAME = "EMPTY.txt";
    private static final List<String> EMPTY = new ArrayList<>();

    @BeforeAll
    public static void createFiles() throws IOException {
        createWordsFile(WORDS_FILE_NAME, WORDS);
        createWordsFile(EMPTY_FILE_NAME, EMPTY);
    }

    private static void createWordsFile(String filename, List<String> words) throws IOException {
        Writer writer = new FileWriter(filename);
        for (String word : words) {
            writer.write(word + "\n");
        }
        writer.close();
    }

    @AfterAll
    public static void deleteFiles() throws IOException {
        Files.deleteIfExists(Paths.get(WORDS_FILE_NAME));
        Files.deleteIfExists(Paths.get(EMPTY_FILE_NAME));
    }

    @Test
    public void constructor() {
        WordleDictionaryLoader loader = createLoader(WORDS_FILE_NAME);
        assertEquals(WORDS_FILE_NAME, loader.getFileName());
        assertEquals("WordleDictionaryLoader", loader.getSenderName());
    }

    private WordleDictionaryLoader createLoader(String fileName) {
        WordleDictionaryLoader loader = new WordleDictionaryLoader(fileName);
        loader.addLogger(Tests.createLogger());
        return loader;
    }

    @Test
    public void load() throws IOException {
        WordleDictionaryLoader loader = createLoader(WORDS_FILE_NAME);
        assertAllWordsExist(loader.load());
    }

    private void assertAllWordsExist(WordleDictionary dictionary) throws IOException {
        for (String word : readAllLines()) {
            assertTrue(dictionary.containsWord(word));
        }
    }

    private List<String> readAllLines() throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(WordleDictionaryLoaderTest.WORDS_FILE_NAME));
        while (reader.ready()) {
            lines.add(reader.readLine());
        }
        reader.close();
        return lines;
    }

    @Test
    public void load_EmptyDictionaryFile() {
        WordleDictionaryLoader loader = createLoader(EMPTY_FILE_NAME);
        assertThrows(EmptyDictionaryFileException.class, loader::load);
    }
}
