package com.luvina.la.controller;
/**
 * Copyright(C) 2026 Luvina Software Company
 * <p>
 * EmployeeController.java, April 13, 2026 tdthang
 */

import com.luvina.la.dto.EmployeeDTO;
import com.luvina.la.payload.EmployeeListResponse;
import com.luvina.la.payload.EmployeeValidationRequest;
import com.luvina.la.payload.EmployeeValidationResponse;
import com.luvina.la.service.EmployeeService;
import com.luvina.la.validator.EmployeeSearchValidator;
import com.luvina.la.validator.EmployeeSearchValidator.EmployeeSearchValidationResult;
import com.luvina.la.validator.EmployeeValidator;
import com.luvina.la.validator.EmployeeValidator.EmployeeValidationResult;
import java.util.Collections;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller xử lý các API nhân viên.
 */
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EmployeeSearchValidator employeeSearchValidator;
    private final EmployeeValidator employeeValidator;

    /**
     * Constructor để inject service và validator phục vụ nghiệp vụ nhân viên.
     *
     * @param employeeService service xử lý nghiệp vụ nhân viên
     * @param employeeSearchValidator validator cho API danh sách nhân viên
     * @param employeeValidator validator cho API add/edit nhân viên
     */
    public EmployeeController(
            EmployeeService employeeService,
            EmployeeSearchValidator employeeSearchValidator,
            EmployeeValidator employeeValidator
    ) {
        this.employeeService = employeeService;
        this.employeeSearchValidator = employeeSearchValidator;
        this.employeeValidator = employeeValidator;
    }

    /**
     * Lấy danh sách nhân viên theo điều kiện tìm kiếm và phân trang.
     *
     * @param departmentId ID phòng ban cần lọc
     * @param employeeName tên nhân viên cần tìm kiếm
     * @param ordEmployeeName thứ tự sắp xếp theo tên nhân viên
     * @param ordCertificationName thứ tự sắp xếp theo tên chứng chỉ
     * @param ordEndDate thứ tự sắp xếp theo ngày hết hạn chứng chỉ
     * @param offsetStr vị trí bắt đầu lấy dữ liệu
     * @param limitStr số bản ghi tối đa cần lấy
     * @return response chứa danh sách nhân viên hoặc lỗi validate/hệ thống
     */
    @GetMapping
    public ResponseEntity<EmployeeListResponse> getListEmployees(
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
                        EmployeeListResponse.error(
                                validationResult.getErrorCode(),
                                validationResult.getErrorParams()
                        )
                );
            }

            String normalizedName = validationResult.getNormalizedEmployeeName();
            Long totalRecords = employeeService.getTotalEmployees(departmentId, normalizedName);

            if (totalRecords == 0) {
                return ResponseEntity.ok(EmployeeListResponse.success(totalRecords, Collections.emptyList(), "MSG005"));
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

            return ResponseEntity.ok(EmployeeListResponse.success(totalRecords, employees));
        } catch (Exception exception) {
            return ResponseEntity.ok(EmployeeListResponse.error("ER023", Collections.emptyList()));
        }
    }

    /**
     * Validate dữ liệu add/edit nhân viên trước khi chuyển sang màn confirm.
     *
     * @param request dữ liệu nhân viên cần validate
     * @return response thành công hoặc lỗi validate
     */
    @PostMapping("/validate")
    public ResponseEntity<EmployeeValidationResponse> validateEmployeeInput(
            @RequestBody(required = false) EmployeeValidationRequest request
    ) {
        try {
            EmployeeValidationResult validationResult = employeeValidator.validate(request);
            if (!validationResult.isValid()) {
                return ResponseEntity.ok(
                        EmployeeValidationResponse.error(
                                validationResult.getErrorCode(),
                                validationResult.getErrorParams()
                        )
                );
            }

            return ResponseEntity.ok(EmployeeValidationResponse.success());
        } catch (Exception exception) {
            return ResponseEntity.ok(EmployeeValidationResponse.error("ER023", Collections.emptyList()));
        }
    }

    /**
     * Thêm mới nhân viên sau khi validate lại dữ liệu submit.
     *
     * @param request dữ liệu nhân viên cần thêm mới
     * @return response thành công hoặc lỗi validate/hệ thống
     */
    @PostMapping
    public ResponseEntity<EmployeeValidationResponse> addEmployee(
            @RequestBody(required = false) EmployeeValidationRequest request
    ) {
        try {
            EmployeeValidationResult validationResult = employeeValidator.validate(request);
            if (!validationResult.isValid()) {
                return ResponseEntity.ok(
                        EmployeeValidationResponse.error(
                                validationResult.getErrorCode(),
                                validationResult.getErrorParams()
                        )
                );
            }

            employeeService.addEmployee(request);
            return ResponseEntity.ok(EmployeeValidationResponse.success());
        } catch (Exception exception) {
            return ResponseEntity.ok(EmployeeValidationResponse.error("ER023", Collections.emptyList()));
        }
    }

    /**
     * Cập nhật nhân viên sau khi validate lại dữ liệu submit.
     *
     * @param employeeId ID nhân viên cần cập nhật
     * @param request dữ liệu nhân viên cần cập nhật
     * @return response thành công hoặc lỗi validate/hệ thống
     */
    @PutMapping("/{employeeId}")
    public ResponseEntity<EmployeeValidationResponse> updateEmployee(
            @PathVariable Long employeeId,
            @RequestBody(required = false) EmployeeValidationRequest request
    ) {
        try {
            EmployeeValidationRequest safeRequest = request == null ? new EmployeeValidationRequest() : request;
            safeRequest.setEmployeeId(String.valueOf(employeeId));

            EmployeeValidationResult validationResult = employeeValidator.validate(safeRequest);
            if (!validationResult.isValid()) {
                return ResponseEntity.ok(
                        EmployeeValidationResponse.error(
                                validationResult.getErrorCode(),
                                validationResult.getErrorParams()
                        )
                );
            }

            employeeService.updateEmployee(employeeId, safeRequest);
            return ResponseEntity.ok(EmployeeValidationResponse.success());
        } catch (Exception exception) {
            return ResponseEntity.ok(EmployeeValidationResponse.error("ER023", Collections.emptyList()));
        }
    }
}
