package com.luvina.la.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.luvina.la.payload.request.EmployeeValidationRequest;
import com.luvina.la.payload.response.EmployeeDeleteResponse;
import com.luvina.la.payload.response.EmployeeValidationResponse;
import com.luvina.la.service.EmployeeService;
import com.luvina.la.validator.EmployeeSearchValidator;
import com.luvina.la.validator.EmployeeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @Mock
    private EmployeeSearchValidator employeeSearchValidator;

    @Mock
    private EmployeeValidator employeeValidator;

    private EmployeeController employeeController;

    @BeforeEach
    void setUp() {
        employeeController = new EmployeeController(
                employeeService,
                employeeSearchValidator,
                employeeValidator
        );
    }

    @Test
    void shouldReturnEr001WhenDeleteEmployeeIdIsMissing() {
        ResponseEntity<EmployeeDeleteResponse> response = employeeController.deleteEmployee(null);

        assertEquals(500, response.getBody().getCode());
        assertEquals("ER001", response.getBody().getMessage().getCode());
        verify(employeeService, never()).deleteEmployee(anyLong());
    }

    @Test
    void shouldReturnEr014WhenDeleteEmployeeIdIsEmpty() {
        ResponseEntity<EmployeeDeleteResponse> response = employeeController.deleteEmployee("");

        assertEquals(500, response.getBody().getCode());
        assertEquals("", response.getBody().getEmployeeId());
        assertEquals("ER014", response.getBody().getMessage().getCode());
        verify(employeeService, never()).deleteEmployee(anyLong());
    }

    @Test
    void shouldReturnEr014WhenDeleteEmployeeIdIsInvalid() {
        ResponseEntity<EmployeeDeleteResponse> response = employeeController.deleteEmployee("abc");

        assertEquals(500, response.getBody().getCode());
        assertEquals("abc", response.getBody().getEmployeeId());
        assertEquals("ER014", response.getBody().getMessage().getCode());
        verify(employeeService, never()).deleteEmployee(anyLong());
    }

    @Test
    void shouldReturnEr014WhenDeleteEmployeeDoesNotExist() {
        when(employeeService.deleteEmployee(30L)).thenReturn(false);

        ResponseEntity<EmployeeDeleteResponse> response = employeeController.deleteEmployee("30");

        assertEquals(500, response.getBody().getCode());
        assertEquals("30", response.getBody().getEmployeeId());
        assertEquals("ER014", response.getBody().getMessage().getCode());
    }

    @Test
    void shouldReturnMsg003WhenDeleteEmployeeSucceeds() {
        when(employeeService.deleteEmployee(30L)).thenReturn(true);

        ResponseEntity<EmployeeDeleteResponse> response = employeeController.deleteEmployee("30");

        assertEquals(200, response.getBody().getCode());
        assertEquals("30", response.getBody().getEmployeeId());
        assertNotNull(response.getBody().getMessage());
        assertEquals("MSG003", response.getBody().getMessage().getCode());
        assertTrue(response.getBody().getMessage().getParams().isEmpty());
    }

    @Test
    void shouldAddEmployeeWithoutSubmitValidation() {
        EmployeeValidationRequest request = new EmployeeValidationRequest();
        when(employeeValidator.validateForConfirm(request)).thenReturn(
                EmployeeValidationResponse.ErrorResponse.valid()
        );
        when(employeeService.addEmployee(request)).thenReturn(30L);

        ResponseEntity<EmployeeValidationResponse> response = employeeController.addEmployee(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(200, response.getBody().getCode());
        assertEquals(30L, response.getBody().getEmployeeId());
        assertEquals("MSG001", response.getBody().getMessage().getCode());
        verify(employeeService).addEmployee(request);
    }

    @Test
    void shouldUpdateEmployeeWithoutSubmitValidation() {
        EmployeeValidationRequest request = new EmployeeValidationRequest();
        request.setEmployeeId("15");

        ResponseEntity<EmployeeValidationResponse> response = employeeController.updateEmployee(request);

        assertEquals(200, response.getBody().getCode());
        verify(employeeService).updateEmployee(15L, request);
        verifyNoInteractions(employeeValidator);
    }
}
