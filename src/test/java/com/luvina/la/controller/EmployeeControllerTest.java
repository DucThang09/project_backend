package com.luvina.la.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.luvina.la.dto.EmployeeDTO;
import com.luvina.la.payload.EmployeeResponse;
import com.luvina.la.service.EmployeeService;
import com.luvina.la.validator.EmployeeSearchValidator;
import com.luvina.la.validator.EmployeeSearchValidator.EmployeeSearchValidationResult;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    private static final String OFFSET_PARAM_NAME = "ã‚ªãƒ•ã‚»ãƒƒãƒˆ";

    @Mock
    private EmployeeService employeeService;

    @Mock
    private EmployeeSearchValidator employeeSearchValidator;

    private EmployeeController employeeController;

    @BeforeEach
    void setUp() {
        employeeController = new EmployeeController(employeeService, employeeSearchValidator);
    }

    @Test
    void shouldReturnValidationErrorWithoutCallingService() {
        when(employeeSearchValidator.validate(" Alice ", "ASC", "DESC", "ASC", "0", "20"))
                .thenReturn(EmployeeSearchValidationResult.invalid("ER018", List.of(OFFSET_PARAM_NAME)));

        ResponseEntity<EmployeeResponse> response = employeeController.getListEmployees(
                1L,
                " Alice ",
                "ASC",
                "DESC",
                "ASC",
                "0",
                "20"
        );

        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getCode());
        assertEquals("ER018", response.getBody().getMessage().getCode());
        assertEquals(List.of(OFFSET_PARAM_NAME), response.getBody().getMessage().getParams());
        verify(employeeService, never()).getTotalEmployees(anyLong(), anyString());
        verify(employeeService, never()).getEmployees(1L, "Alice", "ASC", "DESC", "ASC", 0, 20);
    }

    @Test
    void shouldCallServicesWithNormalizedValues() {
        when(employeeSearchValidator.validate(" Alice ", "ASC", "DESC", "ASC", "0", "20"))
                .thenReturn(EmployeeSearchValidationResult.valid("Alice", 0, 20));
        when(employeeService.getTotalEmployees(1L, "Alice")).thenReturn(1L);
        when(employeeService.getEmployees(1L, "Alice", "ASC", "DESC", "ASC", 0, 20))
                .thenReturn(Collections.<EmployeeDTO>emptyList());

        ResponseEntity<EmployeeResponse> response = employeeController.getListEmployees(
                1L,
                " Alice ",
                "ASC",
                "DESC",
                "ASC",
                "0",
                "20"
        );

        assertNotNull(response.getBody());
        assertEquals(200, response.getBody().getCode());
        assertEquals(1L, response.getBody().getTotalRecords());
        assertTrue(response.getBody().getEmployees().isEmpty());
        verify(employeeService).getTotalEmployees(1L, "Alice");
        verify(employeeService).getEmployees(1L, "Alice", "ASC", "DESC", "ASC", 0, 20);
    }

    @Test
    void shouldReturnMsg005WhenNoEmployeesFound() {
        when(employeeSearchValidator.validate(null, null, null, null, null, null))
                .thenReturn(EmployeeSearchValidationResult.valid(null, 0, 20));
        when(employeeService.getTotalEmployees(null, null)).thenReturn(0L);

        ResponseEntity<EmployeeResponse> response = employeeController.getListEmployees(
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertNotNull(response.getBody());
        assertEquals(200, response.getBody().getCode());
        assertEquals(0L, response.getBody().getTotalRecords());
        assertEquals("MSG005", response.getBody().getMessage().getCode());
        verify(employeeService).getTotalEmployees(null, null);
        verify(employeeService, never()).getEmployees(null, null, null, null, null, 0, 20);
    }

    @Test
    void shouldReturnEr023WhenUnexpectedExceptionOccurs() {
        when(employeeSearchValidator.validate(null, null, null, null, null, null))
                .thenReturn(EmployeeSearchValidationResult.valid(null, 0, 20));
        doThrow(new RuntimeException("boom")).when(employeeService).getTotalEmployees(null, null);

        ResponseEntity<EmployeeResponse> response = employeeController.getListEmployees(
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getCode());
        assertEquals("ER023", response.getBody().getMessage().getCode());
    }
}
