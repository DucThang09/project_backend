package com.luvina.la.controller;
/**
 * Copyright(C) 2026 Luvina Software Company
 * <p>
 * DepartmentController.java, April 13, 2026 tdthang
 */
import com.luvina.la.dto.EmployeeDTO;
import com.luvina.la.dto.EmployeeDetailDTO;
import com.luvina.la.payload.EmployeeDeleteResponse;
import com.luvina.la.payload.EmployeeDetailResponse;
import com.luvina.la.payload.EmployeeListResponse;
import com.luvina.la.payload.EmployeeValidationRequest;
import com.luvina.la.payload.EmployeeValidationResponse;
import com.luvina.la.payload.EmployeeValidationResponse.ErrorResponse;
import com.luvina.la.service.EmployeeService;
import com.luvina.la.validator.EmployeeSearchValidator;
import com.luvina.la.validator.EmployeeSearchValidator.EmployeeSearchValidationResult;
import com.luvina.la.validator.EmployeeValidator;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
 */
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private static final String ID_PARAM_NAME = "ＩＤ";

    /** Service xử lý truy vấn dữ liệu nhân viên. */
    private final EmployeeService employeeService;

    /** Validator kiểm tra tham số tìm kiếm danh sách nhân viên. */
    private final EmployeeSearchValidator employeeSearchValidator;

    /** Validator kiểm tra dữ liệu thêm mới và cập nhật nhân viên. */
    private final EmployeeValidator employeeValidator;

    /**
     * Khởi tạo controller với các service và validator cần dùng cho luồng nhân viên.
     *
     * @param employeeService service xử lý nghiệp vụ nhân viên
     * @param employeeSearchValidator validator cho API danh sach nhan vien
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
        try {
            // Kiểm tra các tham số tìm kiếm, sắp xếp và phân trang trước khi query DB.
            EmployeeSearchValidationResult validationResult = employeeSearchValidator.validate(
                    ordEmployeeName,
                    ordCertificationName,
                    ordEndDate,
                    offsetStr,
                    limitStr
            );

            // Nếu tham số không hợp lệ thì trả response lỗi validate cho frontend.
            if (!validationResult.isValid()) {
                return ResponseEntity.ok(
                        EmployeeListResponse.error(
                                validationResult.getErrorCode(),
                                validationResult.getErrorParams()
                        )
                );
            }

            // Đếm tổng số nhân viên thỏa mãn điều kiện tìm kiếm.
            Long totalRecords = employeeService.getTotalEmployees(departmentId, employeeName);

            // Nếu không có bản ghi nào thì trả danh sách rỗng kèm message MSG005.
            if (totalRecords == 0) {
                return ResponseEntity.ok(
                        EmployeeListResponse.success(totalRecords, Collections.emptyList(), "MSG005")
                );
            }

            // Lấy danh sách nhân viên theo điều kiện đã validate, kèm sắp xếp và phân trang.
            List<EmployeeDTO> employees = employeeService.getEmployees(
                    departmentId,
                    employeeName,
                    ordEmployeeName,
                    ordCertificationName,
                    ordEndDate,
                    validationResult.getOffset(),
                    validationResult.getLimit()
            );

            // Trả response thành công cho màn ADM002.
            return ResponseEntity.ok(EmployeeListResponse.success(totalRecords, employees));
        } catch (Exception exception) {
            // Nếu có lỗi ngoài dự kiến thì trả lỗi hệ thống ER023.
            return ResponseEntity.ok(EmployeeListResponse.error("ER023", Collections.emptyList()));
        }
    }

    /**
     * Lấy thông tin chi tiết của một nhân viên theo ID.
     *
     * @param employeeId ID nhân viên cần lấy chi tiết
     * @return response thành công chứa thông tin nhân viên hoặc response lỗi hệ thống
     */
    @GetMapping("/{employeeId}")
    public ResponseEntity<EmployeeDetailResponse> getEmployeeDetail(@PathVariable Long employeeId) {
        try {
            // Lấy thông tin chi tiết nhân viên theo ID từ service.
            Optional<EmployeeDetailDTO> employeeDetail = employeeService.getEmployeeDetail(employeeId);

            // Nếu không tìm thấy nhân viên thì trả lỗi hệ thống.
            if (employeeDetail.isEmpty()) {
                return ResponseEntity.ok(EmployeeDetailResponse.error("ER023", Collections.emptyList()));
            }

            // Trả response thành công cho màn ADM003.
            return ResponseEntity.ok(EmployeeDetailResponse.success(employeeDetail.get()));
        } catch (Exception exception) {
            // Nếu có lỗi ngoài dự kiến thì trả lỗi hệ thống ER023.
            return ResponseEntity.ok(EmployeeDetailResponse.error("ER023", Collections.emptyList()));
        }
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
        try {
            // Validate dữ liệu nhập từ màn ADM004 trước khi cho sang màn ADM005.
            ErrorResponse validationError = employeeValidator.validateForConfirm(request);

            // Nếu có lỗi validate thì trả mã lỗi và params để frontend hiển thị message.
            if (!validationError.isValid()) {
                return ResponseEntity.ok(
                        EmployeeValidationResponse.error(
                                validationError.getCode(),
                                validationError.getParams()
                        )
                );
            }

            // Không có lỗi validate thì response thành công.
            return ResponseEntity.ok(EmployeeValidationResponse.success());
        } catch (Exception exception) {
            // Nếu có lỗi thì trả lỗi hệ thống ER023.
            return ResponseEntity.ok(EmployeeValidationResponse.error("ER023", Collections.emptyList()));
        }
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
        try {
            // Validate lại dữ liệu trước khi thêm mới để tránh dữ liệu không hợp lệ được lưu.
            ErrorResponse validationError = employeeValidator.validateForAdd(request);

            // Nếu có lỗi validate thì trả lỗi cho frontend.
            if (!validationError.isValid()) {
                return ResponseEntity.ok(
                        EmployeeValidationResponse.error(
                                validationError.getCode(),
                                validationError.getParams()
                        )
                );
            }

            // Thêm mới nhân viên và thông tin chứng chỉ nếu có.
            employeeService.addEmployee(request);

            // Trả response thành công cho màn ADM005 để chuyển sang ADM006.
            return ResponseEntity.ok(EmployeeValidationResponse.success());
        } catch (Exception exception) {
            // Nếu có lỗi ngoài dự kiến thì trả lỗi hệ thống ER023.
            return ResponseEntity.ok(EmployeeValidationResponse.error("ER023", Collections.emptyList()));
        }
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
        try {
            // Validate lại dữ liệu trước khi cập nhật.
            ErrorResponse validationError = employeeValidator.validateForEdit(request);

            // Nếu có lỗi validate thì trả lỗi cho frontend.
            if (!validationError.isValid()) {
                return ResponseEntity.ok(
                        EmployeeValidationResponse.error(
                                validationError.getCode(),
                                validationError.getParams()
                        )
                );
            }

            // Cập nhật nhân viên và ghi lại thông tin chứng chỉ nếu có.
            Long employeeId = Long.parseLong(request.getEmployeeId());
            employeeService.updateEmployee(employeeId, request);

            // Trả response thành công cho màn ADM005 để chuyển sang ADM006.
            return ResponseEntity.ok(EmployeeValidationResponse.success());
        } catch (Exception exception) {
            // Nếu có lỗi ngoài dự kiến thì trả lỗi hệ thống ER023.
            return ResponseEntity.ok(EmployeeValidationResponse.error("ER023", Collections.emptyList()));
        }
    }

    @DeleteMapping
    public ResponseEntity<EmployeeDeleteResponse> deleteEmployee(
            @RequestParam(name = "employeeId", required = false) String employeeId
    ) {
        try {
            // Kiểm tra tham số employeeId bắt buộc.
            if (employeeId == null) {
                return ResponseEntity.ok(
                        EmployeeDeleteResponse.error(employeeId, "ER001", List.of(ID_PARAM_NAME))
                );
            }
            // Chuyển employeeId từ chuỗi sang Long để service/database xử lý.
            // trả ER014 nếu nhân viên không tồn tại.
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
            // Trả response thành công khi xóa hoàn tất.
            return ResponseEntity.ok(EmployeeDeleteResponse.success(employeeId));
        } catch (Exception exception) {
            // Trả lỗi hệ thống khi có exception ngoài dự kiến.
            return ResponseEntity.ok(
                    EmployeeDeleteResponse.error(employeeId, "ER023", Collections.emptyList())
            );
        }
    }
}

