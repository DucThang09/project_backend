package com.luvina.la.controller;
/**
 * Copyright(C) 2026 Luvina Software Company
 * <p>
 * EmployeeController.java, April 13, 2026 tdthang
 */

import com.luvina.la.dto.EmployeeDTO;
import com.luvina.la.payload.EmployeeResponse;
import com.luvina.la.service.EmployeeService;
import com.luvina.la.validator.EmployeeSearchValidator;
import com.luvina.la.validator.EmployeeSearchValidator.EmployeeSearchValidationResult;
import java.util.Collections;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles employee management endpoints.
 * Exposes the employee list API with search and sort conditions.
 */
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EmployeeSearchValidator employeeSearchValidator;

    /**
     * Creates an employee controller.
     *
     * @param employeeService employee service
     * @param employeeSearchValidator employee search validator
     */
    public EmployeeController(
            EmployeeService employeeService,
            EmployeeSearchValidator employeeSearchValidator
    ) {
        this.employeeService = employeeService;
        this.employeeSearchValidator = employeeSearchValidator;
    }

    /**
     * Returns the employee list for the given search and sort params.
     * The controller delegates validation and normalization to the validator,
     * then uses the normalized values to call the service layer.
     *
     * @param departmentId department id filter
     * @param employeeName employee name filter
     * @param ordEmployeeName employee name sort order
     * @param ordCertificationName certification name sort order
     * @param ordEndDate certification end date sort order
     * @param offsetStr page offset
     * @param limitStr page size
     * @return response containing the employee list or an error payload
     */
    @GetMapping
    public ResponseEntity<EmployeeResponse> getListEmployees(
            @RequestParam(name = "department_id", required = false) Long departmentId,
            @RequestParam(name = "employee_name", required = false) String employeeName,
            @RequestParam(name = "ord_employee_name", required = false) String ordEmployeeName,
            @RequestParam(name = "ord_certification_name", required = false) String ordCertificationName,
            @RequestParam(name = "ord_end_date", required = false) String ordEndDate,
            @RequestParam(name = "offset", required = false) String offsetStr,
            @RequestParam(name = "limit", required = false) String limitStr) {

        try {
            EmployeeSearchValidationResult validationResult = employeeSearchValidator.validate(
                    employeeName,
                    ordEmployeeName,
                    ordCertificationName,
                    ordEndDate,
                    offsetStr,
                    limitStr
            );

            if (!validationResult.isValid()) {
                return ResponseEntity.ok(
                        EmployeeResponse.error(
                                validationResult.getErrorCode(),
                                validationResult.getErrorParams()
                        )
                );
            }

            String normalizedName = validationResult.getNormalizedEmployeeName();
            Long totalRecords = employeeService.getTotalEmployees(departmentId, normalizedName);

            if (totalRecords == 0) {
                return ResponseEntity.ok(EmployeeResponse.success(totalRecords, Collections.emptyList(), "MSG005"));
            }

            List<EmployeeDTO> employees = employeeService.getEmployees(
                    departmentId,
                    normalizedName,
                    ordEmployeeName,
                    ordCertificationName,
                    ordEndDate,
                    validationResult.getOffset(),
                    validationResult.getLimit()
            );

            return ResponseEntity.ok(EmployeeResponse.success(totalRecords, employees));
        } catch (Exception exception) {
            return ResponseEntity.ok(EmployeeResponse.error("ER023", Collections.emptyList()));
        }
    }
}
