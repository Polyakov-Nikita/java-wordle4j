package ru.yandex.practicum.dictionary;

public interface WordNormalizer {
    boolean needToNormalize(String word);

    String normalize(String word);
}
