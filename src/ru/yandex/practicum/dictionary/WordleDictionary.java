package ru.yandex.practicum.dictionary;

import ru.yandex.practicum.dictionary.exceptions.EmptyAllowedWordsException;
import ru.yandex.practicum.dictionary.exceptions.EmptyDictionaryException;
import ru.yandex.practicum.dictionary.exceptions.WordAlreadyExcludedException;
import ru.yandex.practicum.dictionary.exceptions.WordIsAbsentException;
import ru.yandex.practicum.log.Loggable;
import ru.yandex.practicum.log.Logger;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

/*
этот класс содержит в себе список слов List<String>
    его методы похожи на методы списка, но учитывают особенности игры
    также этот класс может содержать рутинные функции по сравнению слов, букв и т.д.
 */
public class WordleDictionary implements Loggable {
    private final WordsChecker checker;
    private final WordNormalizer normalizer;
    private final Random random;
    private final List<String> words;
    private final Map<Integer, List<Integer>> masksIndexes;
    private final List<Integer> excludedIndexes;
    private Logger logger;

    public WordleDictionary(WordsChecker checker, WordNormalizer normalizer, int seed) {
        this.checker = checker;
        this.normalizer = normalizer;
        this.random = new Random(seed);
        words = new ArrayList<>();
        masksIndexes = new HashMap<>();
        excludedIndexes = new ArrayList<>();
    }

    @Override
    public void addLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public String getSenderName() {
        return "WordleDictionary";
    }

    public void add(String word) {
        word = normalizeWord(word);
        if (checkWord(word)) {
            addWordToIndex(word);
        }
    }

    private void addWordToIndex(String word) {
        int wordIndex = words.size();
        words.add(word);
        int wordMask = createMask(word);
        masksIndexes.computeIfAbsent(wordMask, k -> new ArrayList<>()).add(wordIndex);
    }

    private int createMask(String word) {
        int wordMask = 0;
        for (char c : word.toCharArray()) {
            int position = c - 'а';
            wordMask |= (1 << position);
        }
        return wordMask;
    }

    public boolean containsWord(String word) {
        return words.contains(word);
    }

    public String getRandomWord() throws IOException, EmptyDictionaryException, EmptyAllowedWordsException {
        checkEmptiness();
        checkAllowed();
        String word = getRandomWord(getRandomIndexList());
        logger.log(this, String.format("Сгенерировано слово '%s'.", word));
        return word;
    }

    private void checkEmptiness() throws EmptyDictionaryException {
        if (words.isEmpty()) {
            throw new EmptyDictionaryException();
        }
    }

    private void checkAllowed() throws EmptyAllowedWordsException {
        if (masksIndexes.isEmpty()) {
            throw new EmptyAllowedWordsException();
        }
    }

    private List<Integer> getRandomIndexList() {
        Set<Integer> keys = masksIndexes.keySet();
        int maskIndex = random.nextInt(keys.size());
        Integer mask = (Integer) keys.toArray()[maskIndex];
        return masksIndexes.get(mask);
    }

    private String getRandomWord(List<Integer> indexList) {
        int randomIndex = random.nextInt(indexList.size());
        int wordIndex = indexList.get(randomIndex);
        return words.get(wordIndex);
    }

    public void excludeWord(String word) throws IOException, WordIsAbsentException, WordAlreadyExcludedException {
        checkAbsent(word);
        checkExcluded(word);
        int wordIndex = words.indexOf(word);
        removeFromAllowed(wordIndex);
        excludedIndexes.add(wordIndex);
        logger.log(this, String.format("Слово '%s' исключено из списка возможных.", word));
    }

    private void checkAbsent(String word) throws WordIsAbsentException {
        if (!words.contains(word)) {
            throw new WordIsAbsentException();
        }
    }

    private void checkExcluded(String word) throws WordAlreadyExcludedException {
        if (excludedIndexes.contains(words.indexOf(word))) {
            throw new WordAlreadyExcludedException(word);
        }
    }

    private void removeFromAllowed(int wordIndex) {
        int wordMask = createMask(words.get(wordIndex));
        List<Integer> wordMaskIndexes = masksIndexes.get(wordMask);
        if (wordMaskIndexes != null) {
            if (wordMaskIndexes.size() == 1) {
                masksIndexes.remove(wordMask);
            } else {
                wordMaskIndexes.remove(Integer.valueOf(wordIndex));
            }
        }
    }

    public boolean isExcludedWord(String word) {
        return excludedIndexes.contains(words.indexOf(word));
    }

    public void removeWithoutLetters(Set<Character> requiredLetters) throws IOException {
        int requiredMask = createMask(requiredLetters);
        Set<Integer> indexesToRemove = getIndexesToRemove((wordMask) -> (wordMask & requiredMask) != requiredMask);
        removeAllFromAllowed(indexesToRemove);
        logger.log(this, String.format("Слова без букв '%s' исключены из списка возможных.", requiredLetters));
    }

    private Set<Integer> getIndexesToRemove(Predicate<Integer> condition) {
        Set<Integer> indexesToRemove = new HashSet<>();
        for (Map.Entry<Integer, List<Integer>> entry : masksIndexes.entrySet()) {
            int wordMask = entry.getKey();
            List<Integer> wordIndexes = entry.getValue();
            if (condition.test(wordMask)) {
                indexesToRemove.addAll(wordIndexes);
            }
        }
        return indexesToRemove;
    }

    private void removeAllFromAllowed(Set<Integer> indexesToRemove) {
        for (int wordIndex : indexesToRemove) {
            removeFromAllowed(wordIndex);
        }
    }

    private int createMask(Set<Character> letters) {
        return createMask(createWord(letters));
    }

    private String createWord(Set<Character> letters) {
        StringBuilder wordBuilder = new StringBuilder();
        for (char letter : letters) {
            wordBuilder.append(letter);
        }
        return wordBuilder.toString();
    }

    public void removeWithLetters(Set<Character> forbiddenLetters) throws IOException {
        List<Integer> forbiddenMasks = createForbiddenMasks(forbiddenLetters);
        Set<Integer> indexesToRemove = getIndexesToRemove((wordMask) -> {
            for (int mask : forbiddenMasks) {
                if ((wordMask & mask) != 0) {
                    return true;
                }
            }
            return false;
        });
        removeAllFromAllowed(indexesToRemove);
        logger.log(this, String.format("Слова с буквами '%s' исключены из списка возможных.", forbiddenLetters));
    }

    private List<Integer> createForbiddenMasks(Set<Character> forbiddenLetters) {
        List<Integer> forbiddenMasks = new ArrayList<>();
        for (char letter : forbiddenLetters) {
            forbiddenMasks.add(createMask(String.valueOf(letter)));
        }
        return forbiddenMasks;
    }

    public String normalizeWord(String word) {
        if (normalizer.needToNormalize(word)) {
            word = normalizer.normalize(word);
        }
        return word;
    }

    public boolean checkWord(String word) {
        return checker.checkWord(word);
    }
}
