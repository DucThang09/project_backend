package com.luvina.la.validator;

import static com.luvina.la.util.ValidationUtils.escapeLikePattern;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class ValidationUtilsTest {

    @Test
    void shouldEscapeLikePatternWildcards() {
        assertEquals("A\\%\\_\\\\", escapeLikePattern(" A%_\\ "));
    }

    @Test
    void shouldReturnNullWhenLikePatternIsNull() {
        assertNull(escapeLikePattern(null));
    }

    @Test
    void shouldReturnEmptyWhenLikePatternIsBlank() {
        assertEquals("", escapeLikePattern("   "));
    }
}
