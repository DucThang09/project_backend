package com.luvina.la.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.luvina.la.config.Constants;
import com.luvina.la.dto.EmployeeDTO;
import com.luvina.la.dto.EmployeeDetailDTO;
import com.luvina.la.entity.Certification;
import com.luvina.la.entity.Department;
import com.luvina.la.entity.Employee;
import com.luvina.la.entity.EmployeeCertification;
import com.luvina.la.payload.EmployeeValidationRequest;
import com.luvina.la.repository.CertificationRepository;
import com.luvina.la.repository.DepartmentRepository;
import com.luvina.la.repository.EmployeeCertificationRepository;
import com.luvina.la.repository.EmployeeRepository;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private CertificationRepository certificationRepository;

    @Mock
    private EmployeeCertificationRepository employeeCertificationRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private EmployeeServiceImpl employeeService;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeServiceImpl(
                employeeRepository,
                departmentRepository,
                certificationRepository,
                employeeCertificationRepository,
                passwordEncoder
        );
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

    @Test
    void shouldReturnEmployeeDetailWhenEmployeeExists() {
        Department department = new Department();
        department.setDepartmentId(2L);
        department.setDepartmentName("Development");

        Certification certification = new Certification();
        certification.setCertificationId(1L);
        certification.setCertificationName("N1");
        certification.setCertificationLevel(1);

        EmployeeCertification employeeCertification = new EmployeeCertification();
        employeeCertification.setEmployeeCertificationId(10L);
        employeeCertification.setCertification(certification);
        employeeCertification.setStartDate(LocalDate.of(2020, 1, 1));
        employeeCertification.setEndDate(LocalDate.of(2022, 1, 1));
        employeeCertification.setScore(new BigDecimal("850"));

        Employee employee = new Employee();
        employee.setEmployeeId(30L);
        employee.setEmployeeLoginId("user01");
        employee.setDepartment(department);
        employee.setEmployeeName("Test User");
        employee.setEmployeeNameKana("ﾃｽﾄ");
        employee.setEmployeeBirthDate(LocalDate.of(2000, 1, 1));
        employee.setEmployeeEmail("test@example.com");
        employee.setEmployeeTelephone("0123456789");
        employee.setRole("USER");
        employee.setEmployeeCertifications(List.of(employeeCertification));

        when(employeeRepository.findById(30L)).thenReturn(Optional.of(employee));

        Optional<EmployeeDetailDTO> result = employeeService.getEmployeeDetail(30L);

        assertTrue(result.isPresent());
        assertEquals("user01", result.get().getEmployeeLoginId());
        assertEquals("Development", result.get().getDepartmentName());
        assertEquals("N1", result.get().getCertificationName());
    }

    @Test
    void shouldReturnEmptyWhenEmployeeDoesNotExist() {
        when(employeeRepository.findById(7L)).thenReturn(Optional.empty());

        Optional<EmployeeDetailDTO> result = employeeService.getEmployeeDetail(7L);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenEmployeeIsAdmin() {
        Department department = new Department();
        department.setDepartmentId(1L);
        department.setDepartmentName("Admin");

        Employee employee = new Employee();
        employee.setEmployeeId(1L);
        employee.setDepartment(department);
        employee.setRole("ADMIN");

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        Optional<EmployeeDetailDTO> result = employeeService.getEmployeeDetail(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmployeeDetailWithoutCertificationWhenEmployeeHasNoCertification() {
        Department department = new Department();
        department.setDepartmentId(2L);
        department.setDepartmentName("Development");

        Employee employee = new Employee();
        employee.setEmployeeId(7L);
        employee.setEmployeeLoginId("user06");
        employee.setDepartment(department);
        employee.setEmployeeName("Bui Thu Ha");
        employee.setEmployeeNameKana("ﾌﾞｲ");
        employee.setEmployeeBirthDate(LocalDate.of(1994, 6, 30));
        employee.setEmployeeEmail("ha06@example.com");
        employee.setEmployeeTelephone("0901000006");
        employee.setRole("USER");
        employee.setEmployeeCertifications(Collections.emptyList());

        when(employeeRepository.findById(7L)).thenReturn(Optional.of(employee));

        Optional<EmployeeDetailDTO> result = employeeService.getEmployeeDetail(7L);

        assertTrue(result.isPresent());
        assertEquals("user06", result.get().getEmployeeLoginId());
        assertEquals(null, result.get().getCertificationName());
    }

    @Test
    void shouldAddEmployeeAndCertification() {
        EmployeeValidationRequest request = createRequest();
        Department department = new Department();
        department.setDepartmentId(2L);
        Certification certification = new Certification();
        certification.setCertificationId(1L);
        Employee savedEmployee = new Employee();
        savedEmployee.setEmployeeId(30L);

        when(departmentRepository.findById(2L)).thenReturn(Optional.of(department));
        when(certificationRepository.findById(1L)).thenReturn(Optional.of(certification));
        when(passwordEncoder.encode("secret123")).thenReturn("encoded-password");
        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);

        employeeService.addEmployee(request);

        verify(employeeRepository).save(any(Employee.class));
        verify(employeeCertificationRepository).save(any());
    }

    @Test
    void shouldUpdateEmployeeAndDeleteCertificationWhenCertificationIsBlank() {
        EmployeeValidationRequest request = createRequest();
        request.setCertificationId("");
        request.setCertificationStartDate(null);
        request.setCertificationEndDate(null);
        request.setScore("");

        Department department = new Department();
        department.setDepartmentId(2L);
        Employee employee = new Employee();
        employee.setEmployeeId(30L);

        when(employeeRepository.findById(30L)).thenReturn(Optional.of(employee));
        when(departmentRepository.findById(2L)).thenReturn(Optional.of(department));
        employeeService.updateEmployee(30L, request);

        verify(employeeCertificationRepository).deleteByEmployeeEmployeeId(30L);
        verify(certificationRepository, never()).findById(any());
    }

    @Test
    void shouldDeleteEmployeeAndCertification() {
        Employee employee = new Employee();
        employee.setEmployeeId(30L);
        employee.setRole("USER");
        when(employeeRepository.findById(30L)).thenReturn(Optional.of(employee));

        boolean result = employeeService.deleteEmployee(30L);

        assertTrue(result);
        verify(employeeCertificationRepository).deleteByEmployeeEmployeeId(30L);
        verify(employeeRepository).delete(employee);
    }

    @Test
    void shouldReturnFalseWhenDeleteEmployeeDoesNotExist() {
        when(employeeRepository.findById(30L)).thenReturn(Optional.empty());

        boolean result = employeeService.deleteEmployee(30L);

        assertFalse(result);
        verify(employeeCertificationRepository, never()).deleteByEmployeeEmployeeId(30L);
        verify(employeeRepository, never()).delete(any());
    }

    private EmployeeValidationRequest createRequest() {
        EmployeeValidationRequest request = new EmployeeValidationRequest();
        request.setDepartmentId("2");
        request.setEmployeeName("Test User");
        request.setEmployeeNameKana("ﾃｽﾄ");
        request.setEmployeeBirthDate("2000/01/01");
        request.setEmployeeEmail("test@example.com");
        request.setEmployeeTelephone("0123456789");
        request.setEmployeeLoginId("user01");
        request.setEmployeeLoginPassword("secret123");
        request.setEmployeeLoginPasswordConfirm("secret123");
        request.setCertificationId("1");
        request.setCertificationStartDate("2020/01/01");
        request.setCertificationEndDate("2022/01/01");
        request.setScore("850");
        return request;
    }
}
