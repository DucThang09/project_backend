/**
 * Copyright(C) 2026 Luvina Software Company
 *
 * EmployeeValidator.java, 10/05/2026 tdthang
 */
package com.luvina.la.validator;

import static com.luvina.la.config.Constants.ACCOUNT_NAME;
import static com.luvina.la.config.Constants.BIRTH_DATE;
import static com.luvina.la.config.Constants.CERTIFICATION;
import static com.luvina.la.config.Constants.CERTIFICATION_END_DATE;
import static com.luvina.la.config.Constants.CERTIFICATION_START_DATE;
import static com.luvina.la.config.Constants.EMAIL;
import static com.luvina.la.config.Constants.EMPLOYEE_NAME;
import static com.luvina.la.config.Constants.EMPLOYEE_NAME_KANA;
import static com.luvina.la.config.Constants.GROUP;
import static com.luvina.la.config.Constants.PASSWORD;
import static com.luvina.la.config.Constants.POSITIVE_INTEGER_PATTERN;
import static com.luvina.la.config.Constants.SCORE;
import static com.luvina.la.config.Constants.TELEPHONE;
import static com.luvina.la.util.ValidationUtils.isHalfsizeNumber;
import static com.luvina.la.util.ValidationUtils.isValidDateFormat;
import static com.luvina.la.util.ValidationUtils.isValidEmail;
import static com.luvina.la.util.ValidationUtils.isValidKatakana;
import static com.luvina.la.util.ValidationUtils.isValidLoginId;
import static com.luvina.la.util.ValidationUtils.isValidMaxLength;
import static com.luvina.la.util.ValidationUtils.isValidMinLength;

import com.luvina.la.entity.Employee;
import com.luvina.la.payload.request.EmployeeValidationRequest;
import com.luvina.la.payload.response.EmployeeValidationResponse.ErrorResponse;
import com.luvina.la.repository.CertificationRepository;
import com.luvina.la.repository.DepartmentRepository;
import com.luvina.la.repository.EmployeeRepository;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Lớp EmployeeValidator chứa các logic kiểm tra dữ liệu đầu vào.
 * @author tdthang
 */
@Component
public class EmployeeValidator {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final CertificationRepository certificationRepository;

    /**
     * Constructor để check exist LoginID, Department, Certification.
     *
     * @param employeeRepository      Repo employee
     * @param departmentRepository    Repo department
     * @param certificationRepository Repo certification
     */
    public EmployeeValidator(
            EmployeeRepository employeeRepository,
            DepartmentRepository departmentRepository,
            CertificationRepository certificationRepository
    ) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.certificationRepository = certificationRepository;
    }

    /**
     * Validate toàn bộ thông tin nhân viên.
     *
     * @param request Thông tin nhân viên cần validate
     * @return ErrorResponse chứa mã lỗi nếu có lỗi, hoặc success nếu hợp lệ
     */
    public ErrorResponse validate(EmployeeValidationRequest request) {

        return validateForConfirm(request);
    }
    /**
     * Kiểm tra dữ liệu validateForConfirm.
     *
     * @param request tham số đầu vào của method
     * @return giá trị trả về sau khi xử lý
     */

    public ErrorResponse validateForConfirm(EmployeeValidationRequest request) {
        if (request == null) {
            return buildResponse("ER023");
        }
        String employeeId = request.getEmployeeId();
        boolean isEditMode = employeeId != null && !employeeId.isBlank();
        return validateEmployeeInput(request, isEditMode);
    }
    /**
     * Kiểm tra dữ liệu validateEmployeeInput.
     *
     * @param request tham số đầu vào của method
     * @param isEditMode tham số đầu vào của method
     * @return giá trị trả về sau khi xử lý
     */

    private ErrorResponse validateEmployeeInput(EmployeeValidationRequest request, boolean isEditMode) {
        ErrorResponse errorResponse;
        // Mode edit không cho sửa account và password nên bỏ qua validate các field đó.
        // Khi thêm mới, kiểm tra account name có rỗng, sai format hoặc bị trùng không.
        if (!isEditMode) {
            errorResponse = validateLoginId(request.getEmployeeLoginId());
            if (errorResponse != null) {
                return errorResponse;
            }
        }

        // Kiểm tra các thông tin cơ bản bắt buộc của nhân viên.
        errorResponse = validateEmployeeName(request.getEmployeeName());
        if (errorResponse != null) {
            return errorResponse;
        }

        errorResponse = validateNameKana(request.getEmployeeNameKana());
        if (errorResponse != null) {
            return errorResponse;
        }

        errorResponse = validateBirthDate(request.getEmployeeBirthDate());
        if (errorResponse != null) {
            return errorResponse;
        }

        errorResponse = validateEmail(request.getEmployeeEmail());
        if (errorResponse != null) {
            return errorResponse;
        }

        errorResponse = validateTelephone(request.getEmployeeTelephone());
        if (errorResponse != null) {
            return errorResponse;
        }

        // Khi thêm mới, bắt buộc nhập password hợp lệ.
        if (!isEditMode) {
            errorResponse = validatePassword(request.getEmployeeLoginPassword());
            if (errorResponse != null) {
                return errorResponse;
            }
        }

        // Kiểm tra phòng ban có hợp lệ và tồn tại trong DB không.
        errorResponse = validateDepartment(request.getDepartmentId());
        if (errorResponse != null) {
            return errorResponse;
        }

        // Nếu người dùng chọn chứng chỉ thì kiểm tra toàn bộ nhóm thông tin chứng chỉ.
        errorResponse = validateCertification(request);
        if (errorResponse != null) {
            return errorResponse;
        }

        // Không có lỗi nào thì trả kết quả validate thành công.
        return ErrorResponse.valid();
    }

    /**
     * Xây dựng phản hồi lỗi.
     *
     * @param errorCode Mã lỗi
     * @param params    Tham số lỗi
     * @return ErrorResponse chứa mã lỗi và tham số lỗi
     */
    public ErrorResponse buildResponse(String errorCode, List<String> params) {
        return ErrorResponse.invalid(errorCode, params);
    }

    /**
     * Xây dựng phản hồi lỗi không có tham số.
     *
     * @param errorCode Mã lỗi
     * @return ErrorResponse chứa mã lỗi
     */
    public ErrorResponse buildResponse(String errorCode) {
        return buildResponse(errorCode, Collections.emptyList());
    }

    /**
     * Validate Login ID.
     *
     * @param employeeLoginId Login ID cần validate
     * @param employeeId      ID nhân viên hiện tại khi sửa
     * @return ErrorResponse chứa mã lỗi nếu có lỗi, hoặc null nếu không có lỗi
     */
    public ErrorResponse validateLoginId(String employeeLoginId) {
        // Chuẩn hóa loginId trước khi kiểm tra.
        if (employeeLoginId == null || employeeLoginId.isEmpty()) {
            return buildResponse("ER001", List.of(ACCOUNT_NAME));
        } else if (!isValidMaxLength(employeeLoginId, 50)) {
            return buildResponse("ER006", List.of(ACCOUNT_NAME, "50"));
        } else if (!isValidLoginId(employeeLoginId)) {
            return buildResponse("ER019");
        } else {
            // Kiểm tra account name đã tồn tại chưa.
            Optional<Employee> existingEmployee = employeeRepository.findByEmployeeLoginId(employeeLoginId);
            if (existingEmployee.isPresent()) {
                return buildResponse("ER003", List.of(ACCOUNT_NAME));
            }
        }
        return null;
    }

    /**
     * Validate Employee Name.
     *
     * @param employeeName Tên nhân viên cần validate
     * @return ErrorResponse chứa mã lỗi nếu có lỗi, hoặc null nếu không có lỗi
     */
    public ErrorResponse validateEmployeeName(String employeeName) {
        if (employeeName == null) {
            return buildResponse("ER001", List.of(EMPLOYEE_NAME));
        }

        String trimmedName = employeeName.trim();
        if (trimmedName.isEmpty()) {
            return buildResponse("ER001", List.of(EMPLOYEE_NAME));
        } else if (!isValidMaxLength(trimmedName, 125)) {
            return buildResponse("ER006", List.of(EMPLOYEE_NAME, "125"));
        }
        return null;
    }

    /**
     * Validate Name Kana.
     *
     * @param employeeNameKana Tên nhân viên Kana cần validate
     * @return ErrorResponse chứa mã lỗi nếu có lỗi, hoặc null nếu không có lỗi
     */
    public ErrorResponse validateNameKana(String employeeNameKana) {
        if (employeeNameKana == null) {
            return buildResponse("ER001", List.of(EMPLOYEE_NAME_KANA));
        }

        String trimmedNameKana = employeeNameKana.trim();
        if (trimmedNameKana.isEmpty()) {
            return buildResponse("ER001", List.of(EMPLOYEE_NAME_KANA));
        } else if (!isValidMaxLength(trimmedNameKana, 125)) {
            return buildResponse("ER006", List.of(EMPLOYEE_NAME_KANA, "125"));
        } else if (!isValidKatakana(trimmedNameKana)) {
            return buildResponse("ER009", List.of(EMPLOYEE_NAME_KANA));
        }
        return null;
    }

    /**
     * Validate Birth Date.
     *
     * @param employeeBirthDate Ngày sinh cần validate
     * @return ErrorResponse chứa mã lỗi nếu có lỗi, hoặc null nếu không có lỗi
     */
    public ErrorResponse validateBirthDate(String employeeBirthDate) {
        if (employeeBirthDate == null) {
            return buildResponse("ER001", List.of(BIRTH_DATE));
        }

        String trimmedBirthDate = employeeBirthDate.trim();
        if (trimmedBirthDate.isEmpty()) {
            return buildResponse("ER001", List.of(BIRTH_DATE));
        } else if (!isValidDateFormat(trimmedBirthDate)) {
            return buildResponse("ER005", List.of(BIRTH_DATE, "yyyy-MM-dd"));
        }
        return null;
    }

    /**
     * Validate Email.
     *
     * @param employeeEmail Email cần validate
     * @return ErrorResponse chứa mã lỗi nếu có lỗi, hoặc null nếu không có lỗi
     */
    public ErrorResponse validateEmail(String employeeEmail) {
        if (employeeEmail == null) {
            return buildResponse("ER001", List.of(EMAIL));
        }

        String trimmedEmail = employeeEmail.trim();
        if (trimmedEmail.isEmpty()) {
            return buildResponse("ER001", List.of(EMAIL));
        } else if (!isValidMaxLength(trimmedEmail, 125)) {
            return buildResponse("ER006", List.of(EMAIL, "125"));
        } else if (!isValidEmail(trimmedEmail)) {
            return buildResponse("ER005", List.of(EMAIL, "xxx@xxx.xxx"));
        }
        return null;
    }

    /**
     * Validate Telephone.
     *
     * @param employeeTelephone Số điện thoại cần validate
     * @return ErrorResponse chứa mã lỗi nếu có lỗi, hoặc null nếu không có lỗi
     */
    public ErrorResponse validateTelephone(String employeeTelephone) {
        if (employeeTelephone == null) {
            return buildResponse("ER001", List.of(TELEPHONE));
        }

        String trimmedTelephone = employeeTelephone.trim();
        if (trimmedTelephone.isEmpty()) {
            return buildResponse("ER001", List.of(TELEPHONE));
        } else if (!isValidMaxLength(trimmedTelephone, 50)) {
            return buildResponse("ER006", List.of(TELEPHONE, "50"));
        } else if (!isHalfsizeNumber(trimmedTelephone)) {
            return buildResponse("ER008", List.of(TELEPHONE));
        }
        return null;
    }

    /**
     * Validate Password.
     *
     * @param employeeLoginPassword Mật khẩu cần validate
     * @return ErrorResponse chứa mã lỗi nếu có lỗi, hoặc null nếu không có lỗi
     */
    public ErrorResponse validatePassword(String employeeLoginPassword) {
        if (employeeLoginPassword == null || employeeLoginPassword.isEmpty()) {
            return buildResponse("ER001", List.of(PASSWORD));
        } else if (!isValidMinLength(employeeLoginPassword, 8)
                || !isValidMaxLength(employeeLoginPassword, 50)) {
            return buildResponse("ER007", List.of(PASSWORD, "8", "50"));
        }
        return null;
    }

    /**
     * Validate Department.
     *
     * @param departmentId ID phòng ban cần validate
     * @return ErrorResponse chứa mã lỗi nếu có lỗi, hoặc null nếu không có lỗi
     */
    public ErrorResponse validateDepartment(String departmentId) {
        // Chuẩn hóa departmentId trước khi kiểm tra rỗng, số nguyên dương và tồn tại DB.
        if (departmentId == null) {
            return buildResponse("ER002", List.of(GROUP));
        }

        String trimmedDepartmentId = departmentId.trim();
        Long departmentIdValue = null;

        // Chỉ parse sang Long khi departmentId đúng format số nguyên dương.
        if (trimmedDepartmentId != null
                && !trimmedDepartmentId.isEmpty()
                && POSITIVE_INTEGER_PATTERN.matcher(trimmedDepartmentId).matches()) {
            try {
                departmentIdValue = Long.parseLong(trimmedDepartmentId);
            } catch (NumberFormatException ex) {
                departmentIdValue = null;
            }
        }

        if (trimmedDepartmentId.isEmpty()) {
            return buildResponse("ER002", List.of(GROUP));
        } else if (departmentIdValue == null) {
            return buildResponse("ER018", List.of(GROUP));
        } else if (!departmentRepository.existsById(departmentIdValue)) {
            // ID đúng format nhưng không tồn tại trong bảng phòng ban.
            return buildResponse("ER004", List.of(GROUP));
        }
        return null;
    }

    /**
     * Validate Certification.
     *
     * @param request EmployeeValidationRequest chứa thông tin chứng chỉ
     * @return ErrorResponse chứa mã lỗi nếu có lỗi, hoặc null nếu không có lỗi
     */
    public ErrorResponse validateCertification(EmployeeValidationRequest request) {
        // Nếu không chọn chứng chỉ thì bỏ qua toàn bộ validate nhóm chứng chỉ.
        String certificationId = request.getCertificationId();
        if (certificationId == null) {
            return null;
        }

        certificationId = certificationId.trim();
        if (certificationId.isEmpty()) {
            return null;
        }

        Long certificationIdValue = null;

        // Chuyển certificationId sang Long khi đúng format số nguyên dương.
        if (certificationId != null && !certificationId.isEmpty() && POSITIVE_INTEGER_PATTERN.matcher(certificationId).matches()) {
            try {
                certificationIdValue = Long.parseLong(certificationId);
            } catch (NumberFormatException ex) {
                certificationIdValue = null;
            }
        }
        String certificationStartDate = null;
        String certificationStartDateRaw = request.getCertificationStartDate();
        if (certificationStartDateRaw != null) {
            certificationStartDate = certificationStartDateRaw.trim();
        }

        String certificationEndDate = null;
        String certificationEndDateRaw = request.getCertificationEndDate();
        if (certificationEndDateRaw != null) {
            certificationEndDate = certificationEndDateRaw.trim();
        }

        String score = null;
        String scoreRaw = request.getScore();
        if (scoreRaw != null) {
            score = scoreRaw.trim();
        }

        // Kiểm tra chứng chỉ tồn tại và các field liên quan có đầy đủ, đúng format không.
        if (certificationIdValue == null) {
            return buildResponse("ER018", List.of(CERTIFICATION));
        } else if (!certificationRepository.existsById(certificationIdValue)) {
            return buildResponse("ER004", List.of(CERTIFICATION));
        } else if (certificationStartDate == null || certificationStartDate.isEmpty()) {
            return buildResponse("ER001", List.of(CERTIFICATION_START_DATE));
        } else if (!isValidDateFormat(certificationStartDate)) {
            return buildResponse("ER005", List.of(CERTIFICATION_START_DATE, "yyyy-MM-dd"));
        } else if (certificationEndDate == null || certificationEndDate.isEmpty()) {
            return buildResponse("ER001", List.of(CERTIFICATION_END_DATE));
        } else if (!isValidDateFormat(certificationEndDate)) {
            return buildResponse("ER005", List.of(CERTIFICATION_END_DATE, "yyyy-MM-dd"));
        } else if (score == null || score.isEmpty()) {
            return buildResponse("ER001", List.of(SCORE));
        } else if (!isHalfsizeNumber(score)) {
            return buildResponse("ER018", List.of(SCORE));
        } else {
            LocalDate start;
            LocalDate end;
            try {
                // Parse ngày để so sánh ngày hết hạn phải lớn hơn ngày cấp.
                start = LocalDate.parse(certificationStartDate);
                end = LocalDate.parse(certificationEndDate);
            } catch (DateTimeParseException ex) {
                return buildResponse("ER005", List.of(CERTIFICATION_END_DATE, "yyyy-MM-dd"));
            }
            if (start == null || end == null) {
                return buildResponse("ER005", List.of(CERTIFICATION_END_DATE, "yyyy-MM-dd"));
            } else if (!end.isAfter(start)) {
                // Ngày hết hạn phải sau ngày cấp chứng chỉ.
                return buildResponse("ER012", Collections.emptyList());
            }
        }

        return null;
    }
}
