package com.luvina.la.validator;
/**
 * Copyright(C) 2026 Luvina Software Company
 * <p>
 * EmployeeController.java, April 13, 2026 tdthang
 */
import static com.luvina.la.config.Constants.ACCOUNT_NAME;
import static com.luvina.la.config.Constants.ACCOUNT_NAME_PATTERN;
import static com.luvina.la.config.Constants.BIRTH_DATE;
import static com.luvina.la.config.Constants.CERTIFICATION;
import static com.luvina.la.config.Constants.CERTIFICATION_END_DATE;
import static com.luvina.la.config.Constants.CERTIFICATION_START_DATE;
import static com.luvina.la.config.Constants.EMAIL;
import static com.luvina.la.config.Constants.EMAIL_PATTERN;
import static com.luvina.la.config.Constants.EMPLOYEE_NAME;
import static com.luvina.la.config.Constants.EMPLOYEE_NAME_KANA;
import static com.luvina.la.config.Constants.GROUP;
import static com.luvina.la.config.Constants.KATAKANA_PATTERN;
import static com.luvina.la.config.Constants.PASSWORD;
import static com.luvina.la.config.Constants.POSITIVE_INTEGER_PATTERN;
import static com.luvina.la.config.Constants.SCORE;
import static com.luvina.la.config.Constants.TELEPHONE;

import com.luvina.la.entity.Employee;
import com.luvina.la.payload.EmployeeValidationRequest;
import com.luvina.la.payload.EmployeeValidationResponse.ErrorResponse;
import com.luvina.la.repository.CertificationRepository;
import com.luvina.la.repository.DepartmentRepository;
import com.luvina.la.repository.EmployeeRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

/**
 * Lớp EmployeeValidator chứa các logic kiểm tra dữ liệu đầu vào.
 */
@Component
public class EmployeeValidator {

    private static final String HALFSIZE_NUMBER_PATTERN = "^[0-9]*$";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

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
        ErrorResponse errorResponse;
        // Mode edit không cho sửa account và password nên bỏ qua validate các field đó.
        boolean isEditMode = request.getEmployeeId() != null && !request.getEmployeeId().isBlank();

        // Khi thêm mới, kiểm tra account name có rỗng, sai format hoặc bị trùng không.
        if (!isEditMode) {
            errorResponse = validateLoginId(request.getEmployeeLoginId(), request.getEmployeeId());
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
     * Kiểm tra độ dài tối đa của chuỗi.
     *
     * @param value     Chuỗi cần kiểm tra
     * @param maxLength Độ dài tối đa
     * @return true nếu hợp lệ, false nếu không hợp lệ
     */
    public boolean isValidMaxLength(String value, int maxLength) {
        if (value == null) {
            return true;
        }
        return value.length() <= maxLength;
    }

    /**
     * Kiểm tra độ dài tối thiểu của chuỗi.
     *
     * @param value     Chuỗi cần kiểm tra
     * @param minLength Độ dài tối thiểu
     * @return true nếu hợp lệ, false nếu không hợp lệ
     */
    public boolean isValidMinLength(String value, int minLength) {
        if (value == null) {
            return false;
        }
        return value.length() >= minLength;
    }

    /**
     * Kiểm tra định dạng Login ID (ER019).
     *
     * @param loginId Login ID cần kiểm tra
     * @return true nếu hợp lệ, false nếu không hợp lệ
     */
    public boolean isValidLoginId(String loginId) {
        if (loginId == null || loginId.isEmpty()) {
            return true;
        }
        return ACCOUNT_NAME_PATTERN.matcher(loginId).matches();
    }

    /**
     * Kiểm tra định dạng Email (ER005).
     *
     * @param email Email cần kiểm tra
     * @return true nếu hợp lệ, false nếu không hợp lệ
     */
    public boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return true;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Kiểm tra định dạng Katakana (ER009).
     *
     * @param text Chuỗi cần kiểm tra
     * @return true nếu hợp lệ, false nếu không hợp lệ
     */
    public boolean isValidKatakana(String text) {
        if (text == null || text.isEmpty()) {
            return true;
        }
        return KATAKANA_PATTERN.matcher(text).matches();
    }

    /**
     * Kiểm tra định dạng số Halfsize (ER008/ER018).
     *
     * @param text Chuỗi cần kiểm tra
     * @return true nếu hợp lệ, false nếu không hợp lệ
     */
    public boolean isHalfsizeNumber(String text) {
        if (text == null || text.isEmpty()) {
            return true;
        }
        return Pattern.matches(HALFSIZE_NUMBER_PATTERN, text);
    }

    /**
     * Kiểm tra định dạng ngày tháng yyyy/MM/dd.
     *
     * @param date Ngày tháng cần kiểm tra
     * @return true nếu hợp lệ, false nếu không hợp lệ
     */
    public boolean isValidDateFormat(String date) {
        if (date == null || date.isEmpty()) {
            return true;
        }

        try {
            String[] parts = date.split("/");
            if (parts.length != 3) {
                return false;
            }

            Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);

            if (month < 1 || month > 12) {
                return false;
            }
            if (day < 1 || day > 31) {
                return false;
            }

            LocalDate.parse(date, DATE_FORMATTER);
            return true;
        } catch (Exception ex) {
            return false;
        }
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
    public ErrorResponse validateLoginId(String employeeLoginId, String employeeId) {
        // Chuẩn hóa loginId trước khi kiểm tra.
        String trimmedLoginId = employeeLoginId == null ? null : employeeLoginId.trim();
        Long currentEmployeeId = null;

        // Khi edit, chuyển employeeId hiện tại sang Long để bỏ qua chính bản ghi đang sửa khi check trùng.
        if (employeeId != null && !employeeId.isEmpty() && POSITIVE_INTEGER_PATTERN.matcher(employeeId).matches()) {
            try {
                currentEmployeeId = Long.parseLong(employeeId);
            } catch (NumberFormatException ex) {
                currentEmployeeId = null;
            }
        }

        if (trimmedLoginId == null || trimmedLoginId.isEmpty()) {
            return buildResponse("ER001", List.of(ACCOUNT_NAME));
        } else if (!isValidMaxLength(trimmedLoginId, 50)) {
            return buildResponse("ER006", List.of(ACCOUNT_NAME, "50"));
        } else if (!isValidLoginId(trimmedLoginId)) {
            return buildResponse("ER019");
        } else {
            // Kiểm tra account name đã tồn tại ở nhân viên khác chưa.
            Optional<Employee> existingEmployee = employeeRepository.findByEmployeeLoginId(trimmedLoginId);
            if (existingEmployee.isPresent()
                    && (currentEmployeeId == null
                    || !existingEmployee.get().getEmployeeId().equals(currentEmployeeId))) {
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
        String trimmedName = employeeName == null ? null : employeeName.trim();
        if (trimmedName == null || trimmedName.isEmpty()) {
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
        String trimmedNameKana = employeeNameKana == null ? null : employeeNameKana.trim();
        if (trimmedNameKana == null || trimmedNameKana.isEmpty()) {
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
        String trimmedBirthDate = employeeBirthDate == null ? null : employeeBirthDate.trim();
        if (trimmedBirthDate == null || trimmedBirthDate.isEmpty()) {
            return buildResponse("ER001", List.of(BIRTH_DATE));
        } else if (!isValidDateFormat(trimmedBirthDate)) {
            return buildResponse("ER005", List.of(BIRTH_DATE, "yyyy/MM/dd"));
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
        String trimmedEmail = employeeEmail == null ? null : employeeEmail.trim();
        if (trimmedEmail == null || trimmedEmail.isEmpty()) {
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
        String trimmedTelephone = employeeTelephone == null ? null : employeeTelephone.trim();
        if (trimmedTelephone == null || trimmedTelephone.isEmpty()) {
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
        String trimmedDepartmentId = departmentId == null ? null : departmentId.trim();
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

        if (trimmedDepartmentId == null || trimmedDepartmentId.isEmpty()) {
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
        String certificationId = request.getCertificationId() == null ? null : request.getCertificationId().trim();
        if (certificationId == null || certificationId.isEmpty()) {
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
        String certificationStartDate = request.getCertificationStartDate() == null
                ? null : request.getCertificationStartDate().trim();
        String certificationEndDate = request.getCertificationEndDate() == null
                ? null : request.getCertificationEndDate().trim();
        String score = request.getScore() == null ? null : request.getScore().trim();

        // Kiểm tra chứng chỉ tồn tại và các field liên quan có đầy đủ, đúng format không.
        if (certificationIdValue == null) {
            return buildResponse("ER018", List.of(CERTIFICATION));
        } else if (!certificationRepository.existsById(certificationIdValue)) {
            return buildResponse("ER004", List.of(CERTIFICATION));
        } else if (certificationStartDate == null || certificationStartDate.isEmpty()) {
            return buildResponse("ER001", List.of(CERTIFICATION_START_DATE));
        } else if (!isValidDateFormat(certificationStartDate)) {
            return buildResponse("ER005", List.of(CERTIFICATION_START_DATE, "yyyy/MM/dd"));
        } else if (certificationEndDate == null || certificationEndDate.isEmpty()) {
            return buildResponse("ER001", List.of(CERTIFICATION_END_DATE));
        } else if (!isValidDateFormat(certificationEndDate)) {
            return buildResponse("ER005", List.of(CERTIFICATION_END_DATE, "yyyy/MM/dd"));
        } else if (score == null || score.isEmpty()) {
            return buildResponse("ER001", List.of(SCORE));
        } else if (!isHalfsizeNumber(score)) {
            return buildResponse("ER018", List.of(SCORE));
        } else {
            LocalDate start;
            LocalDate end;
            try {
                // Parse ngày để so sánh ngày hết hạn phải lớn hơn ngày cấp.
                start = LocalDate.parse(certificationStartDate, DATE_FORMATTER);
                end = LocalDate.parse(certificationEndDate, DATE_FORMATTER);
            } catch (DateTimeParseException ex) {
                return buildResponse("ER005", List.of(CERTIFICATION_END_DATE, "yyyy/MM/dd"));
            }
            if (start == null || end == null) {
                return buildResponse("ER005", List.of(CERTIFICATION_END_DATE, "yyyy/MM/dd"));
            } else if (!end.isAfter(start)) {
                // Ngày hết hạn phải sau ngày cấp chứng chỉ.
                return buildResponse("ER012", Collections.emptyList());
            }
        }

        return null;
    }
}
