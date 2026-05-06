package com.luvina.la.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.luvina.la.payload.request.EmployeeValidationRequest;
import com.luvina.la.payload.response.EmployeeValidationResponse.ErrorResponse;
import com.luvina.la.repository.CertificationRepository;
import com.luvina.la.repository.DepartmentRepository;
import com.luvina.la.repository.EmployeeRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmployeeValidatorTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private CertificationRepository certificationRepository;

    private EmployeeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new EmployeeValidator(
                employeeRepository,
                departmentRepository,
                certificationRepository
        );
    }

    @Test
    void shouldReturnRequiredErrorWhenLoginIdIsEmpty() {
        EmployeeValidationRequest request = validRequest();
        request.setEmployeeLoginId("");

        ErrorResponse result = validator.validate(request);

        assertEquals("ER001", result.getCode());
    }

    @Test
    void shouldReturnFormatErrorWhenLoginIdIsWhitespace() {
        EmployeeValidationRequest request = validRequest();
        request.setEmployeeLoginId(" ");

        ErrorResponse result = validator.validate(request);

        assertEquals("ER019", result.getCode());
    }

    @Test
    void shouldReturnEr023WhenConfirmRequestIsNull() {
        ErrorResponse result = validator.validateForConfirm(null);

        assertEquals("ER023", result.getCode());
    }

    @Test
    void shouldSkipAccountAndPasswordForConfirmEdit() {
        EmployeeValidationRequest request = validRequest();
        request.setEmployeeId("1");
        request.setEmployeeLoginId(" ");
        request.setEmployeeLoginPassword("");
        when(departmentRepository.existsById(1L)).thenReturn(true);
        when(certificationRepository.existsById(1L)).thenReturn(true);

        ErrorResponse result = validator.validateForConfirm(request);

        assertTrue(result.isValid());
        assertEquals("200", result.getCode());
    }

    @Test
    void shouldValidateAccountAndPasswordForConfirmAdd() {
        EmployeeValidationRequest request = validRequest();
        request.setEmployeeLoginId(" ");
        request.setEmployeeLoginPassword("");

        ErrorResponse result = validator.validateForConfirm(request);

        assertEquals("ER019", result.getCode());
    }

    @Test
    void shouldReturnEr003WhenLoginIdAlreadyExists() {
        EmployeeValidationRequest request = validRequest();
        when(employeeRepository.findByEmployeeLoginId("user01"))
                .thenReturn(Optional.of(new com.luvina.la.entity.Employee()));

        ErrorResponse result = validator.validate(request);

        assertEquals("ER003", result.getCode());
    }

    @Test
    void shouldReturnEr004WhenDepartmentDoesNotExist() {
        EmployeeValidationRequest request = validRequest();
        when(employeeRepository.findByEmployeeLoginId("user01")).thenReturn(Optional.empty());
        when(departmentRepository.existsById(1L)).thenReturn(false);

        ErrorResponse result = validator.validate(request);

        assertEquals("ER004", result.getCode());
        assertEquals("\u30b0\u30eb\u30fc\u30d7", result.getParams().get(0));
    }

    @Test
    void shouldReturnEr004WhenCertificationDoesNotExist() {
        EmployeeValidationRequest request = validRequest();
        when(employeeRepository.findByEmployeeLoginId("user01")).thenReturn(Optional.empty());
        when(departmentRepository.existsById(1L)).thenReturn(true);
        when(certificationRepository.existsById(1L)).thenReturn(false);

        ErrorResponse result = validator.validate(request);

        assertEquals("ER004", result.getCode());
        assertEquals("\u8cc7\u683c", result.getParams().get(0));
    }

    @Test
    void shouldReturnValidWhenAllValidationPasses() {
        when(employeeRepository.findByEmployeeLoginId("user01")).thenReturn(Optional.empty());
        when(departmentRepository.existsById(1L)).thenReturn(true);
        when(certificationRepository.existsById(1L)).thenReturn(true);

        ErrorResponse result = validator.validate(validRequest());

        assertTrue(result.isValid());
        assertEquals("200", result.getCode());
    }

    @Test
    void shouldAcceptHalfWidthKatakanaNameKana() {
        EmployeeValidationRequest request = validRequest();
        request.setEmployeeNameKana("\uff76\uff80\uff76\uff85");
        when(employeeRepository.findByEmployeeLoginId("user01")).thenReturn(Optional.empty());
        when(departmentRepository.existsById(1L)).thenReturn(true);
        when(certificationRepository.existsById(1L)).thenReturn(true);

        ErrorResponse result = validator.validate(request);

        assertTrue(result.isValid());
        assertEquals("200", result.getCode());
    }

    @Test
    void shouldReturnEr009WhenNameKanaIsFullWidthKatakana() {
        EmployeeValidationRequest request = validRequest();
        request.setEmployeeNameKana("\u30ab\u30bf\u30ab\u30ca");
        when(employeeRepository.findByEmployeeLoginId("user01")).thenReturn(Optional.empty());

        ErrorResponse result = validator.validate(request);

        assertEquals("ER009", result.getCode());
    }

    private EmployeeValidationRequest validRequest() {
        EmployeeValidationRequest request = new EmployeeValidationRequest();
        request.setEmployeeLoginId("user01");
        request.setDepartmentId("1");
        request.setEmployeeName("Test User");
        request.setEmployeeNameKana("\uff83\uff7d\uff84");
        request.setEmployeeBirthDate("2000-01-01");
        request.setEmployeeEmail("test@example.com");
        request.setEmployeeTelephone("0123456789");
        request.setEmployeeLoginPassword("secret123");
        request.setEmployeeLoginPasswordConfirm("secret123");
        request.setCertificationId("1");
        request.setCertificationStartDate("2020-01-01");
        request.setCertificationEndDate("2022-01-01");
        request.setScore("850");
        return request;
    }
}
