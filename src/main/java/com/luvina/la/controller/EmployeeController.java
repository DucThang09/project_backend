/**
 * Copyright(C) 2026 Luvina Software Company
 *
 * EmployeeController.java, 10/05/2026 tdthang
 */
package com.luvina.la.controller;

import static com.luvina.la.config.Constants.ID_PARAM_NAME;

import com.luvina.la.dto.EmployeeDTO;
import com.luvina.la.dto.EmployeeDetailDTO;
import com.luvina.la.payload.request.EmployeeValidationRequest;
import com.luvina.la.payload.response.EmployeeDeleteResponse;
import com.luvina.la.payload.response.EmployeeDetailResponse;
import com.luvina.la.payload.response.EmployeeListResponse;
import com.luvina.la.payload.response.EmployeeValidationResponse;
import com.luvina.la.payload.response.EmployeeValidationResponse.ErrorResponse;
import com.luvina.la.service.EmployeeService;
import com.luvina.la.validator.EmployeeSearchValidator;
import com.luvina.la.validator.EmployeeSearchValidator.EmployeeSearchValidationResult;
import com.luvina.la.validator.EmployeeValidator;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller xử lý các API liên quan đến nhân viên.
 * Bao gồm danh sách, chi tiết, validate, thêm mới và cập nhật nhân viên.
 * @author tdthang
 */
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EmployeeSearchValidator employeeSearchValidator;
    private final EmployeeValidator employeeValidator;

    /**
     * Khởi tạo controller với các service và validator cần dùng cho luồng nhân viên.
     *
     * @param employeeService service xử lý nghiệp vụ nhân viên
     * @param employeeSearchValidator validator cho API danh sách nhân viên
     * @param employeeValidator validator cho API thêm mới và cập nhật nhân viên
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
     * Lấy danh sách nhân viên theo điều kiện tìm kiếm, sắp xếp và phân trang.
     *
     * @param departmentId ID phòng ban cần lọc
     * @param employeeName tên nhân viên cần tìm kiếm
     * @param ordEmployeeName thứ tự sắp xếp theo tên nhân viên
     * @param ordCertificationName thứ tự sắp xếp theo tên chứng chỉ
     * @param ordEndDate thứ tự sắp xếp theo ngày hết hạn chứng chỉ
     * @param offsetStr vị trí bắt đầu lấy dữ liệu
     * @param limitStr số bản ghi tối đa cần lấy
     * @return response chứa danh sách nhân viên hoặc thông tin lỗi
     */
    @GetMapping
    public ResponseEntity<EmployeeListResponse> getListEmployees(
            @RequestParam(name = "department_id", required = false) Long departmentId,
            @RequestParam(name = "employee_name", required = false) String employeeName,
            @RequestParam(name = "ord_employee_name", required = false) String ordEmployeeName,
            @RequestParam(name = "ord_certification_name", required = false) String ordCertificationName,
            @RequestParam(name = "ord_end_date", required = false) String ordEndDate,
            @RequestParam(name = "offset", required = false) String offsetStr,
            @RequestParam(name = "limit", required = false) String limitStr
    ) {
        EmployeeSearchValidationResult validationResult = employeeSearchValidator.validate(
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

        Long totalRecords = employeeService.getTotalEmployees(departmentId, employeeName);
        if (totalRecords == 0) {
            return ResponseEntity.ok(
                    EmployeeListResponse.success(totalRecords, Collections.emptyList(), "MSG005")
            );
        }

        List<EmployeeDTO> employees = employeeService.getEmployees(
                departmentId,
                employeeName,
                ordEmployeeName,
                ordCertificationName,
                ordEndDate,
                validationResult.getOffset(),
                validationResult.getLimit()
        );
        return ResponseEntity.ok(EmployeeListResponse.success(totalRecords, employees));
    }

    /**
     * Lấy thông tin chi tiết của một nhân viên theo ID.
     *
     * @param employeeId ID nhân viên cần lấy chi tiết
     * @return response thành công chứa thông tin nhân viên hoặc response lỗi hệ thống
     */
    @GetMapping("/{employeeId}")
    public ResponseEntity<EmployeeDetailResponse> getEmployeeDetail(@PathVariable Long employeeId) {
        Optional<EmployeeDetailDTO> employeeDetail = employeeService.getEmployeeDetail(employeeId);
        if (employeeDetail.isEmpty()) {
            return ResponseEntity.ok(EmployeeDetailResponse.error("ER023", Collections.emptyList()));
        }
        return ResponseEntity.ok(EmployeeDetailResponse.success(employeeDetail.get()));
    }

    /**
     * Validate dữ liệu thêm mới hoặc cập nhật nhân viên trước khi chuyển sang màn confirm.
     *
     * @param request dữ liệu nhân viên cần validate
     * @return response thành công hoặc response chứa lỗi validate
     */
    @PostMapping("/validate")
    public ResponseEntity<EmployeeValidationResponse> validateEmployeeInput(
            @RequestBody(required = false) EmployeeValidationRequest request
    ) {
        ErrorResponse validationError = employeeValidator.validateForConfirm(request);
        if (!validationError.isValid()) {
            return ResponseEntity.ok(
                    EmployeeValidationResponse.error(
                            validationError.getCode(),
                            validationError.getParams()
                    )
            );
        }
        return ResponseEntity.ok(EmployeeValidationResponse.success());
    }

    /**
     * Thêm mới nhân viên sau khi validate lại dữ liệu.
     *
     * @param request dữ liệu nhân viên cần thêm mới
     * @return response thành công hoặc response chứa lỗi validate/hệ thống
     */
    @PostMapping
    public ResponseEntity<EmployeeValidationResponse> addEmployee(
            @RequestBody(required = false) EmployeeValidationRequest request
    ) {
        ErrorResponse validationError = employeeValidator.validateForConfirm(request);
        if (!validationError.isValid()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    EmployeeValidationResponse.error(
                            validationError.getCode(),
                            validationError.getParams()
                    )
            );
        }

        Long employeeId = employeeService.addEmployee(request);
        return ResponseEntity.ok(EmployeeValidationResponse.success(employeeId, "MSG001"));
    }

    /**
     * Cập nhật nhân viên sau khi validate lại dữ liệu submit.
     *
     * @param request dữ liệu nhân viên cần cập nhật
     * @return response thành công hoặc response chứa lỗi validate/hệ thống
     */
    @PutMapping
    public ResponseEntity<EmployeeValidationResponse> updateEmployee(
            @RequestBody(required = false) EmployeeValidationRequest request
    ) {
        Long employeeId = Long.parseLong(request.getEmployeeId());
        employeeService.updateEmployee(employeeId, request);
        return ResponseEntity.ok(EmployeeValidationResponse.success());
    }

    /**
     * Xóa dữ liệu deleteEmployee.
     *
     * @param employeeId tham số đầu vào của method
     * @return giá trị trả về sau khi xử lý
     */
    @DeleteMapping
    public ResponseEntity<EmployeeDeleteResponse> deleteEmployee(
            @RequestParam(name = "employeeId", required = false) String employeeId
    ) {
        if (employeeId == null) {
            return ResponseEntity.ok(
                    EmployeeDeleteResponse.error(employeeId, "ER001", List.of(ID_PARAM_NAME))
            );
        }

        Long employeeIdValue;
        try {
            employeeIdValue = Long.parseLong(employeeId);
        } catch (NumberFormatException exception) {
            return ResponseEntity.ok(
                    EmployeeDeleteResponse.error(employeeId, "ER014", List.of(ID_PARAM_NAME))
            );
        }

        boolean deleted = employeeService.deleteEmployee(employeeIdValue);
        if (!deleted) {
            return ResponseEntity.ok(
                    EmployeeDeleteResponse.error(employeeId, "ER014", List.of(ID_PARAM_NAME))
            );
        }
        return ResponseEntity.ok(EmployeeDeleteResponse.success(employeeId));
    }
}
