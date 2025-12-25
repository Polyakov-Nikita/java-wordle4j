package ru.yandex.practicum.dictionary;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UpperENormalizerTest {
    private static final String NOT_NEED_TO_NORMALIZE = "world";
    private static final String NEED_TO_NORMALIZE_UPPER_CASE = "Earth";
    private static final String NEED_TO_NORMALIZE_E_CASE = "жёнка";
    private static final String NORMALIZED_UPPER_CASE = "earth";
    private static final String NORMALIZED_E_CASE = "женка";

    private static UpperENormalizer normalizer;

    @BeforeAll
    public static void createNormalizer() {
        normalizer = new UpperENormalizer();
    }

    @Test
    public void needToNormalize_NotNeed() {
        assertFalse(normalizer.needToNormalize(NOT_NEED_TO_NORMALIZE));
    }

    @Test
    public void needToNormalize_UpperCase() {
        assertTrue(normalizer.needToNormalize(NEED_TO_NORMALIZE_UPPER_CASE));
    }

    @Test
    public void needToNormalize_ECase() {
        assertTrue(normalizer.needToNormalize(NEED_TO_NORMALIZE_E_CASE));
    }

    @Test
    public void normalize_UpperCase() {
        assertEquals(NORMALIZED_UPPER_CASE, normalizer.normalize(NEED_TO_NORMALIZE_UPPER_CASE));
    }

    @Test
    public void normalize_ECase() {
        assertEquals(NORMALIZED_E_CASE, normalizer.normalize(NEED_TO_NORMALIZE_E_CASE));
    }
}
