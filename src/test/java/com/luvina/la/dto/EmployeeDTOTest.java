package com.luvina.la.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class EmployeeDTOTest {

    @Test
    void shouldMapAllColumnsFromRow() {
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

        EmployeeDTO dto = EmployeeDTO.fromRow(row);

        assertEquals(1L, dto.getEmployeeId());
        assertEquals("Nguyen Van A", dto.getEmployeeName());
        assertEquals(LocalDate.of(1990, 1, 1), dto.getEmployeeBirthDate());
        assertEquals("Development", dto.getDepartmentName());
        assertEquals("a@example.com", dto.getEmployeeEmail());
        assertEquals("0123456789", dto.getEmployeeTelephone());
        assertEquals("Java", dto.getCertificationName());
        assertEquals(LocalDate.of(2027, 12, 31), dto.getEndDate());
        assertEquals(new BigDecimal("850.50"), dto.getScore());
    }

    @Test
    void shouldHandleNullableColumnsSafely() {
        Object[] row = new Object[] {
                2L,
                "Nguyen Van B",
                null,
                "QA",
                "b@example.com",
                null,
                null,
                null,
                null
        };

        EmployeeDTO dto = EmployeeDTO.fromRow(row);

        assertEquals(2L, dto.getEmployeeId());
        assertEquals("Nguyen Van B", dto.getEmployeeName());
        assertNull(dto.getEmployeeBirthDate());
        assertEquals("QA", dto.getDepartmentName());
        assertEquals("b@example.com", dto.getEmployeeEmail());
        assertNull(dto.getEmployeeTelephone());
        assertNull(dto.getCertificationName());
        assertNull(dto.getEndDate());
        assertNull(dto.getScore());
    }
}
