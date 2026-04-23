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
import static com.luvina.la.config.Constants.HALF_WIDTH_PATTERN;
import static com.luvina.la.config.Constants.KATAKANA_PATTERN;
import static com.luvina.la.config.Constants.PASSWORD;
import static com.luvina.la.config.Constants.POSITIVE_INTEGER_PATTERN;
import static com.luvina.la.config.Constants.SCORE;
import static com.luvina.la.config.Constants.TELEPHONE;

import com.luvina.la.entity.Employee;
import com.luvina.la.payload.EmployeeValidationRequest;
import com.luvina.la.repository.CertificationRepository;
import com.luvina.la.repository.DepartmentRepository;
import com.luvina.la.repository.EmployeeRepository;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class EmployeeValidator {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final CertificationRepository certificationRepository;

    /**
     *
     * @param employeeRepository
     * @param departmentRepository
     * @param certificationRepository
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

    public EmployeeValidationResult validate(EmployeeValidationRequest request) {
        EmployeeValidationRequest safeRequest = request == null ? new EmployeeValidationRequest() : request;

        // Kiểm tra trùng loginId khi thêm/sửa nhân viên.
        String employeeLoginId = trim(safeRequest.getEmployeeLoginId());
        Long currentEmployeeId = parsePositiveLong(trim(safeRequest.getEmployeeId()));
        if (isEmpty(employeeLoginId)) {
            return EmployeeValidationResult.invalid("ER001", List.of(ACCOUNT_NAME));
        }
        if (employeeLoginId.length() > 50) {
            return EmployeeValidationResult.invalid("ER006", List.of(ACCOUNT_NAME, "50"));
        }
        if (!ACCOUNT_NAME_PATTERN.matcher(employeeLoginId).matches()) {
            return EmployeeValidationResult.invalid("ER019", Collections.emptyList());
        }
        Optional<Employee> existingEmployee = employeeRepository.findByEmployeeLoginId(employeeLoginId);
        if (existingEmployee.isPresent()
                && (currentEmployeeId == null
                || !existingEmployee.get().getEmployeeId().equals(currentEmployeeId))) {
            return EmployeeValidationResult.invalid("ER003", List.of(ACCOUNT_NAME));
        }

        // Kiểm tra phòng ban bắt buộc chọn và có tồn tại trong DB.
        String departmentId = trim(safeRequest.getDepartmentId());
        if (isEmpty(departmentId)) {
            return EmployeeValidationResult.invalid("ER002", List.of(GROUP));
        }
        Long departmentIdValue = parsePositiveLong(departmentId);
        if (departmentIdValue == null) {
            return EmployeeValidationResult.invalid("ER018", List.of(GROUP));
        }
        if (!departmentRepository.existsById(departmentIdValue)) {
            return EmployeeValidationResult.invalid("ER004", List.of(GROUP));
        }

        // Kiểm tra các thông tin cơ bản của nhân viên.
        String employeeName = trim(safeRequest.getEmployeeName());
        if (isEmpty(employeeName)) {
            return EmployeeValidationResult.invalid("ER001", List.of(EMPLOYEE_NAME));
        }
        if (employeeName.length() > 125) {
            return EmployeeValidationResult.invalid("ER006", List.of(EMPLOYEE_NAME, "125"));
        }

        String employeeNameKana = trim(safeRequest.getEmployeeNameKana());
        if (isEmpty(employeeNameKana)) {
            return EmployeeValidationResult.invalid("ER001", List.of(EMPLOYEE_NAME_KANA));
        }
        if (employeeNameKana.length() > 125) {
            return EmployeeValidationResult.invalid("ER006", List.of(EMPLOYEE_NAME_KANA, "125"));
        }
        if (!KATAKANA_PATTERN.matcher(employeeNameKana).matches()) {
            return EmployeeValidationResult.invalid("ER009", List.of(EMPLOYEE_NAME_KANA));
        }

        String employeeBirthDate = trim(safeRequest.getEmployeeBirthDate());
        if (isEmpty(employeeBirthDate)) {
            return EmployeeValidationResult.invalid("ER001", List.of(BIRTH_DATE));
        }
        if (parseIsoDate(employeeBirthDate).isEmpty()) {
            return EmployeeValidationResult.invalid("ER005", List.of(BIRTH_DATE, "yyyy-MM-dd"));
        }

        String employeeEmail = trim(safeRequest.getEmployeeEmail());
        if (isEmpty(employeeEmail)) {
            return EmployeeValidationResult.invalid("ER001", List.of(EMAIL));
        }
        if (employeeEmail.length() > 125) {
            return EmployeeValidationResult.invalid("ER006", List.of(EMAIL, "125"));
        }
        if (!EMAIL_PATTERN.matcher(employeeEmail).matches()) {
            return EmployeeValidationResult.invalid("ER005", List.of(EMAIL, "xxx@xxx.xxx"));
        }

        String employeeTelephone = trim(safeRequest.getEmployeeTelephone());
        if (isEmpty(employeeTelephone)) {
            return EmployeeValidationResult.invalid("ER001", List.of(TELEPHONE));
        }
        if (employeeTelephone.length() > 50) {
            return EmployeeValidationResult.invalid("ER006", List.of(TELEPHONE, "50"));
        }
        if (!HALF_WIDTH_PATTERN.matcher(employeeTelephone).matches()) {
            return EmployeeValidationResult.invalid("ER008", List.of(TELEPHONE));
        }

        // Backend chỉ kiểm tra mật khẩu; confirm password được xử lý ở frontend.
        String employeeLoginPassword = defaultString(safeRequest.getEmployeeLoginPassword());
        if (employeeLoginPassword.isEmpty()) {
            return EmployeeValidationResult.invalid("ER001", List.of(PASSWORD));
        }
        if (employeeLoginPassword.length() < 8 || employeeLoginPassword.length() > 50) {
            return EmployeeValidationResult.invalid("ER007", List.of(PASSWORD, "8", "50"));
        }

        // Nếu chưa chọn chứng chỉ thì bỏ qua toàn bộ validation của phần chứng chỉ.
        String certificationId = trim(safeRequest.getCertificationId());
        if (isEmpty(certificationId)) {
            return EmployeeValidationResult.valid();
        }

        // Khi đã chọn chứng chỉ thì các thông tin liên quan phải bắt buộc chọn.
        Long certificationIdValue = parsePositiveLong(certificationId);
        if (certificationIdValue == null) {
            return EmployeeValidationResult.invalid("ER018", List.of(CERTIFICATION));
        }
        if (!certificationRepository.existsById(certificationIdValue)) {
            return EmployeeValidationResult.invalid("ER004", List.of(CERTIFICATION));
        }

        String certificationStartDate = trim(safeRequest.getCertificationStartDate());
        if (isEmpty(certificationStartDate)) {
            return EmployeeValidationResult.invalid("ER001", List.of(CERTIFICATION_START_DATE));
        }
        Optional<LocalDate> certificationStartDateValue = parseIsoDate(certificationStartDate);
        if (certificationStartDateValue.isEmpty()) {
            return EmployeeValidationResult.invalid("ER005", List.of(CERTIFICATION_START_DATE, "yyyy-MM-dd"));
        }

        String certificationEndDate = trim(safeRequest.getCertificationEndDate());
        if (isEmpty(certificationEndDate)) {
            return EmployeeValidationResult.invalid("ER001", List.of(CERTIFICATION_END_DATE));
        }
        Optional<LocalDate> certificationEndDateValue = parseIsoDate(certificationEndDate);
        if (certificationEndDateValue.isEmpty()) {
            return EmployeeValidationResult.invalid("ER005", List.of(CERTIFICATION_END_DATE, "yyyy-MM-dd"));
        }
        if (certificationEndDateValue.get().isBefore(certificationStartDateValue.get())) {
            return EmployeeValidationResult.invalid("ER012", Collections.emptyList());
        }

        // Điểm phải là số nguyên dương khi có chọn chứng chỉ.
        String score = trim(safeRequest.getScore());
        if (isEmpty(score)) {
            return EmployeeValidationResult.invalid("ER001", List.of(SCORE));
        }
        if (!POSITIVE_INTEGER_PATTERN.matcher(score).matches()) {
            return EmployeeValidationResult.invalid("ER018", List.of(SCORE));
        }

        return EmployeeValidationResult.valid();
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    // Giữ nguyên chuỗi gốc để tránh null khi kiểm tra password.
    private String defaultString(String value) {
        return value == null ? "" : value;
    }

    private boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }

    private Long parsePositiveLong(String value) {
        if (isEmpty(value) || !POSITIVE_INTEGER_PATTERN.matcher(value).matches()) {
            return null;
        }

        try {
            return Long.parseLong(value);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private Optional<LocalDate> parseIsoDate(String value) {
        if (isEmpty(value)) {
            return Optional.empty();
        }

        try {
            return Optional.of(LocalDate.parse(value));
        } catch (DateTimeParseException exception) {
            return Optional.empty();
        }
    }

    // kết quả validate để controller biết dữ liệu hợp lệ hay lỗi ở đâu.
    public static class EmployeeValidationResult {

        private final boolean valid;
        private final String errorCode;
        private final List<String> errorParams;

        private EmployeeValidationResult(boolean valid, String errorCode, List<String> errorParams) {
            this.valid = valid;
            this.errorCode = errorCode;
            this.errorParams = errorParams;
        }

        // Trả về kết quả hợp lệ, không kèm mã lỗi.
        public static EmployeeValidationResult valid() {
            return new EmployeeValidationResult(true, null, Collections.emptyList());
        }

        // Trả về kết quả không hợp lệ cùng mã lỗi và tham số để format message.
        public static EmployeeValidationResult invalid(String errorCode, List<String> errorParams) {
            return new EmployeeValidationResult(false, errorCode, errorParams);
        }

        public boolean isValid() {
            return valid;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public List<String> getErrorParams() {
            return errorParams;
        }
    }
}
