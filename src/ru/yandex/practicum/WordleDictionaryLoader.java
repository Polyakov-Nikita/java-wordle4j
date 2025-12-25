package ru.yandex.practicum;

import ru.yandex.practicum.dictionary.FiveCharsChecker;
import ru.yandex.practicum.dictionary.UpperENormalizer;
import ru.yandex.practicum.dictionary.WordleDictionary;
import ru.yandex.practicum.exceptions.CustomIOException;
import ru.yandex.practicum.exceptions.EmptyDictionaryFileException;
import ru.yandex.practicum.log.Loggable;
import ru.yandex.practicum.log.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/*
этот класс содержит в себе всю рутину по работе с файлами словарей и с кодировками
    ему нужны методы по загрузке списка слов из файла по имени файла
    на выходе должен быть класс WordleDictionary
 */
public class WordleDictionaryLoader implements Loggable {
    private final String fileName;

    private Logger logger;

    public WordleDictionaryLoader(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void addLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public String getSenderName() {
        return "WordleDictionaryLoader";
    }

    public String getFileName() {
        return fileName;
    }

    public WordleDictionary load() {
        WordleDictionary dictionary =
                new WordleDictionary(new FiveCharsChecker(), new UpperENormalizer(), LocalTime.now().getNano());
        for (String word : readAllWords()) {
            dictionary.add(word);
        }
        dictionary.addLogger(logger);
        logger.log(this, String.format("Загружен файл словаря '%s'.", fileName));
        return dictionary;
    }

    private List<String> readAllWords() {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            while (reader.ready()) {
                lines.add(reader.readLine());
            }
        } catch (IOException e) {
            throw new CustomIOException(e);
        }
        checkEmptiness(lines);
        return lines;
    }

    private void checkEmptiness(List<String> lines) {
        if (lines.isEmpty()) {
            throw new EmptyDictionaryFileException();
        }
    }
}
