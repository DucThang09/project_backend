package com.luvina.la.controller;
/**
 * Copyright(C) 2026 Luvina Software Company
 * <p>
 * DepartmentController.java, April 13, 2026 tdthang
 */
import com.luvina.la.dto.EmployeeDTO;
import com.luvina.la.payload.EmployeeResponse;
import com.luvina.la.service.EmployeeService;
import java.util.Collections;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class EmployeeController {

    // Giá trị mặc định khi client không truyền lên.
    private static final int DEFAULT_OFFSET = 0;
    private static final int DEFAULT_LIMIT = 20;
    private static final String OFFSET_PARAM_NAME = "オフセット";
    private static final String LIMIT_PARAM_NAME = "リミット";

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/employees")
    public ResponseEntity<EmployeeResponse> getListEmployees(
            @RequestParam(name = "department_id", required = false) Long departmentId,
            @RequestParam(name = "employee_name", required = false) String employeeName,
            @RequestParam(name = "ord_employee_name", required = false) String ordEmployeeName,
            @RequestParam(name = "ord_certification_name", required = false) String ordCertificationName,
            @RequestParam(name = "ord_end_date", required = false) String ordEndDate,
            @RequestParam(name = "offset", required = false) String offsetStr,
            @RequestParam(name = "limit", required = false) String limitStr) {

        try {
            // Tham số sort chỉ chấp nhận ASC hoặc DESC.
            if (isInvalidOrder(ordEmployeeName)
                    || isInvalidOrder(ordCertificationName)
                    || isInvalidOrder(ordEndDate)) {
                return ResponseEntity.ok(EmployeeResponse.error("ER021", Collections.emptyList()));
            }

            // Dừng ngay nếu tham số phân trang không hợp lệ.
            ValidationResult offsetValidation = validateOffset(offsetStr);
            if (!offsetValidation.isValid()) {
                return ResponseEntity.ok(EmployeeResponse.error("ER018", List.of(OFFSET_PARAM_NAME)));
            }

            ValidationResult limitValidation = validateLimit(limitStr);
            if (!limitValidation.isValid()) {
                return ResponseEntity.ok(EmployeeResponse.error("ER018", List.of(LIMIT_PARAM_NAME)));
            }

            String normalizedName = normalizeEmployeeName(employeeName);
            Long totalRecords = employeeService.getTotalEmployees(departmentId, normalizedName);

            // Không có dữ liệu thì trả danh sách rỗng
            if (totalRecords == 0) {
                return ResponseEntity.ok(EmployeeResponse.success(totalRecords, Collections.emptyList(), "MSG005"));
            }

            // Chỉ query danh sách khi toàn bộ tham số đã hợp lệ.
            List<EmployeeDTO> employees = employeeService.getEmployees(
                    departmentId,
                    normalizedName,
                    ordEmployeeName,
                    ordCertificationName,
                    ordEndDate,
                    offsetValidation.getValue(),
                    limitValidation.getValue()
            );

            return ResponseEntity.ok(EmployeeResponse.success(totalRecords, employees));
        } catch (Exception e) {
            // Giữ format response ổn định khi có lỗi ngoài mong muốn.
            return ResponseEntity.ok(EmployeeResponse.error("ER023", Collections.emptyList()));
        }
    }

    // Không truyền sort thì xem như dùng thứ tự mặc định.
    private boolean isInvalidOrder(String order) {
        if (isEmpty(order)) {
            return false;
        }

        return !"ASC".equals(order) && !"DESC".equals(order);
    }

    // offset được phép bằng 0 vì trang đầu bắt đầu từ vị trí 0.
    private ValidationResult validateOffset(String offsetStr) {
        if (isEmpty(offsetStr)) {
            return ValidationResult.valid(DEFAULT_OFFSET);
        }

        try {
            int value = Integer.parseInt(offsetStr);
            return value >= 0 ? ValidationResult.valid(value) : ValidationResult.invalid();
        } catch (NumberFormatException e) {
            return ValidationResult.invalid();
        }
    }

    // limit phải là số nguyên dương.
    private ValidationResult validateLimit(String limitStr) {
        if (isEmpty(limitStr)) {
            return ValidationResult.valid(DEFAULT_LIMIT);
        }

        try {
            int value = Integer.parseInt(limitStr);
            return value > 0 ? ValidationResult.valid(value) : ValidationResult.invalid();
        } catch (NumberFormatException e) {
            return ValidationResult.invalid();
        }
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String normalizeEmployeeName(String employeeName) {
        return isEmpty(employeeName) ? null : employeeName.trim();
    }

    // Gom kết quả validate và giá trị đã parse.
    private static final class ValidationResult {
        private final boolean valid;
        private final Integer value;

        private ValidationResult(boolean valid, Integer value) {
            this.valid = valid;
            this.value = value;
        }

        private static ValidationResult valid(Integer value) {
            return new ValidationResult(true, value);
        }

        private static ValidationResult invalid() {
            return new ValidationResult(false, null);
        }

        private boolean isValid() {
            return valid;
        }

        private Integer getValue() {
            return value;
        }
    }
}
