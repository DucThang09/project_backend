package com.luvina.la.controller;

import com.luvina.la.dto.EmployeeDTO;
import com.luvina.la.dto.EmployeeDetailDTO;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller xu ly cac API nhan vien.
 */
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EmployeeSearchValidator employeeSearchValidator;
    private final EmployeeValidator employeeValidator;

    /**
     * Constructor de inject service va validator phuc vu nghiep vu nhan vien.
     *
     * @param employeeService service xu ly nghiep vu nhan vien
     * @param employeeSearchValidator validator cho API danh sach nhan vien
     * @param employeeValidator validator cho API add/edit nhan vien
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
     * Lay danh sach nhan vien theo dieu kien tim kiem va phan trang.
     *
     * @param departmentId ID phong ban can loc
     * @param employeeName ten nhan vien can tim kiem
     * @param ordEmployeeName thu tu sap xep theo ten nhan vien
     * @param ordCertificationName thu tu sap xep theo ten chung chi
     * @param ordEndDate thu tu sap xep theo ngay het han chung chi
     * @param offsetStr vi tri bat dau lay du lieu
     * @param limitStr so ban ghi toi da can lay
     * @return response chua danh sach nhan vien hoac loi validate
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
                return ResponseEntity.ok(
                        EmployeeListResponse.success(totalRecords, Collections.emptyList(), "MSG005")
                );
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
     * Lay thong tin chi tiet cua mot nhan vien theo ID.
     *
     * @param employeeId ID nhan vien can lay chi tiet
     * @return response thanh cong hoac loi he thong
     */
    @GetMapping("/{employeeId}")
    public ResponseEntity<EmployeeDetailResponse> getEmployeeDetail(@PathVariable Long employeeId) {
        try {
            Optional<EmployeeDetailDTO> employeeDetail = employeeService.getEmployeeDetail(employeeId);

            if (employeeDetail.isEmpty()) {
                return ResponseEntity.ok(EmployeeDetailResponse.error("ER023", Collections.emptyList()));
            }

            return ResponseEntity.ok(EmployeeDetailResponse.success(employeeDetail.get()));
        } catch (Exception exception) {
            return ResponseEntity.ok(EmployeeDetailResponse.error("ER023", Collections.emptyList()));
        }
    }

    /**
     * Validate du lieu add/edit nhan vien truoc khi chuyen sang man confirm.
     *
     * @param request du lieu nhan vien can validate
     * @return response thanh cong hoac loi validate
     */
    @PostMapping("/validate")
    public ResponseEntity<EmployeeValidationResponse> validateEmployeeInput(
            @RequestBody(required = false) EmployeeValidationRequest request
    ) {
        try {
            if (request == null) {
                return ResponseEntity.ok(EmployeeValidationResponse.error("ER023", Collections.emptyList()));
            }

            ErrorResponse validationError = employeeValidator.validate(request);
            if (!validationError.isValid()) {
                return ResponseEntity.ok(
                        EmployeeValidationResponse.error(
                                validationError.getCode(),
                                validationError.getParams()
                        )
                );
            }

            return ResponseEntity.ok(EmployeeValidationResponse.success());
        } catch (Exception exception) {
            return ResponseEntity.ok(EmployeeValidationResponse.error("ER023", Collections.emptyList()));
        }
    }

    /**
     * Them moi nhan vien sau khi validate lai du lieu submit.
     *
     * @param request du lieu nhan vien can them moi
     * @return response thanh cong hoac loi validate/he thong
     */
    @PostMapping
    public ResponseEntity<EmployeeValidationResponse> addEmployee(
            @RequestBody(required = false) EmployeeValidationRequest request
    ) {
        try {
            if (request == null) {
                return ResponseEntity.ok(EmployeeValidationResponse.error("ER023", Collections.emptyList()));
            }

            ErrorResponse validationError = employeeValidator.validate(request);
            if (!validationError.isValid()) {
                return ResponseEntity.ok(
                        EmployeeValidationResponse.error(
                                validationError.getCode(),
                                validationError.getParams()
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
     * Cap nhat nhan vien sau khi validate lai du lieu submit.
     *
     * @param employeeId ID nhan vien can cap nhat
     * @param request du lieu nhan vien can cap nhat
     * @return response thanh cong hoac loi validate/he thong
     */
    @PutMapping("/{employeeId}")
    public ResponseEntity<EmployeeValidationResponse> updateEmployee(
            @PathVariable Long employeeId,
            @RequestBody(required = false) EmployeeValidationRequest request
    ) {
        try {
            if (request == null) {
                return ResponseEntity.ok(EmployeeValidationResponse.error("ER023", Collections.emptyList()));
            }

            request.setEmployeeId(String.valueOf(employeeId));

            ErrorResponse validationError = employeeValidator.validate(request);
            if (!validationError.isValid()) {
                return ResponseEntity.ok(
                        EmployeeValidationResponse.error(
                                validationError.getCode(),
                                validationError.getParams()
                        )
                );
            }

            employeeService.updateEmployee(employeeId, request);
            return ResponseEntity.ok(EmployeeValidationResponse.success());
        } catch (Exception exception) {
            return ResponseEntity.ok(EmployeeValidationResponse.error("ER023", Collections.emptyList()));
        }
    }
}
