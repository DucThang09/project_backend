package com.luvina.la.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.luvina.la.config.Constants;
import com.luvina.la.dto.EmployeeDTO;
import com.luvina.la.repository.EmployeeRepository;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    private EmployeeServiceImpl employeeService;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeServiceImpl(employeeRepository);
    }

    @Test
    void shouldUseDefaultLimitWhenLimitIsNull() {
        Object[] row = new Object[] {
                1L,
                "Nguyen Van A",
                Date.valueOf(LocalDate.of(1990, 1, 1)),
                "Development",
                "a@example.com",
                "0123456789",
                "Java",
                Date.valueOf(LocalDate.of(2027, 12, 31)),
                new BigDecimal("850.50")
        };

        when(employeeRepository.findEmployees(
                null,
                null,
                null,
                null,
                null,
                Constants.DEFAULT_LIMIT,
                Constants.DEFAULT_OFFSET
        )).thenReturn(Collections.singletonList(row));

        List<EmployeeDTO> result = employeeService.getEmployees(null, null, null, null, null, null, null);

        assertEquals(1, result.size());
        assertEquals("Nguyen Van A", result.get(0).getEmployeeName());
        verify(employeeRepository).findEmployees(
                null,
                null,
                null,
                null,
                null,
                Constants.DEFAULT_LIMIT,
                Constants.DEFAULT_OFFSET
        );
    }

    @Test
    void shouldUseProvidedLimitWhenLimitIsPositive() {
        when(employeeRepository.findEmployees(
                null,
                null,
                null,
                null,
                null,
                10,
                5
        )).thenReturn(List.of());

        employeeService.getEmployees(null, null, null, null, null, 5, 10);

        verify(employeeRepository).findEmployees(
                null,
                null,
                null,
                null,
                null,
                10,
                5
        );
    }
}
