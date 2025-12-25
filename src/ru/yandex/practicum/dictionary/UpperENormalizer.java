package ru.yandex.practicum.dictionary;

public class UpperENormalizer implements WordNormalizer {
    @Override
    public boolean needToNormalize(String word) {
        for (char symbol : word.toCharArray()) {
            if (isNormalizableSymbol(symbol)) {
                return true;
            }
        }
        return false;
    }

    private boolean isNormalizableSymbol(char symbol) {
        return Character.isUpperCase(symbol) || symbol == 'ё';
    }

    @Override
    public String normalize(String word) {
        return word.toLowerCase().replace('ё', 'е');
    }
}
