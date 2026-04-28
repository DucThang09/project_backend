package com.luvina.la.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.luvina.la.config.Constants;
import com.luvina.la.validator.EmployeeSearchValidator.EmployeeSearchValidationResult;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EmployeeSearchValidatorTest {

    private static final String OFFSET_PARAM_NAME = "\u30aa\u30d5\u30bb\u30c3\u30c8";
    private static final String LIMIT_PARAM_NAME = "\u30ea\u30df\u30c3\u30c8";

    private EmployeeSearchValidator validator;

    @BeforeEach
    void setUp() {
        validator = new EmployeeSearchValidator();
    }

    @Test
    void shouldAcceptNullOrBlankSortOrders() {
        EmployeeSearchValidationResult result = validator.validate(
                null,
                "",
                "   ",
                null,
                null
        );

        assertTrue(result.isValid());
        assertEquals(Constants.DEFAULT_OFFSET, result.getOffset());
        assertEquals(Constants.DEFAULT_LIMIT, result.getLimit());
    }

    @Test
    void shouldRejectInvalidSortOrder() {
        EmployeeSearchValidationResult result = validator.validate(
                "UP",
                "ASC",
                "DESC",
                "0",
                "20"
        );

        assertFalse(result.isValid());
        assertEquals("ER021", result.getErrorCode());
        assertEquals(Collections.emptyList(), result.getErrorParams());
        assertNull(result.getOffset());
        assertNull(result.getLimit());
    }

    @Test
    void shouldRejectNegativeOffset() {
        EmployeeSearchValidationResult result = validator.validate(
                "ASC",
                "ASC",
                "ASC",
                "-1",
                "20"
        );

        assertFalse(result.isValid());
        assertEquals("ER018", result.getErrorCode());
        assertEquals(List.of(OFFSET_PARAM_NAME), result.getErrorParams());
    }

    @Test
    void shouldRejectNonNumericOffset() {
        EmployeeSearchValidationResult result = validator.validate(
                "ASC",
                "ASC",
                "ASC",
                "abc",
                "20"
        );

        assertFalse(result.isValid());
        assertEquals("ER018", result.getErrorCode());
        assertEquals(List.of(OFFSET_PARAM_NAME), result.getErrorParams());
    }

    @Test
    void shouldRejectInvalidLimit() {
        EmployeeSearchValidationResult result = validator.validate(
                "ASC",
                "ASC",
                "ASC",
                "0",
                "0"
        );

        assertFalse(result.isValid());
        assertEquals("ER018", result.getErrorCode());
        assertEquals(List.of(LIMIT_PARAM_NAME), result.getErrorParams());
    }

    @Test
    void shouldRejectNonNumericLimit() {
        EmployeeSearchValidationResult result = validator.validate(
                "ASC",
                "ASC",
                "ASC",
                "0",
                "abc"
        );

        assertFalse(result.isValid());
        assertEquals("ER018", result.getErrorCode());
        assertEquals(List.of(LIMIT_PARAM_NAME), result.getErrorParams());
    }

    @Test
    void shouldAcceptValidPagination() {
        EmployeeSearchValidationResult result = validator.validate(
                "ASC",
                "DESC",
                "ASC",
                "5",
                "10"
        );

        assertTrue(result.isValid());
        assertEquals(5, result.getOffset());
        assertEquals(10, result.getLimit());
    }
}
