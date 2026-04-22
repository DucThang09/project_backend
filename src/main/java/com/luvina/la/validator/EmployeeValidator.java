package com.luvina.la.validator;

import com.luvina.la.payload.EmployeeValidationRequest;
import com.luvina.la.repository.CertificationRepository;
import com.luvina.la.repository.DepartmentRepository;
import com.luvina.la.repository.EmployeeRepository;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

/**
 * Validates employee input shared by add/edit before the confirm screen.
 */
@Component
public class EmployeeValidator {

    private static final String ACCOUNT_NAME = "アカウント名";
    private static final String GROUP = "グループ";
    private static final String EMPLOYEE_NAME = "氏名";
    private static final String EMPLOYEE_NAME_KANA = "カタカナ氏名";
    private static final String BIRTH_DATE = "生年月日";
    private static final String EMAIL = "メールアドレス";
    private static final String TELEPHONE = "電話番号";
    private static final String PASSWORD = "パスワード";
    private static final String PASSWORD_CONFIRM = "パスワード（確認）";
    private static final String CERTIFICATION = "資格";
    private static final String CERTIFICATION_START_DATE = "資格交付日";
    private static final String CERTIFICATION_END_DATE = "失効日";
    private static final String SCORE = "点数";

    private static final Pattern ACCOUNT_NAME_PATTERN = Pattern.compile("^[A-Za-z_][A-Za-z0-9_]*$");
    private static final Pattern KATAKANA_PATTERN = Pattern.compile("^[ァ-ヶー]+$");
    private static final Pattern HALF_WIDTH_PATTERN = Pattern.compile("^[\\x20-\\x7E]+$");
    private static final Pattern POSITIVE_INTEGER_PATTERN = Pattern.compile("^[1-9]\\d*$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final CertificationRepository certificationRepository;

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

        String employeeLoginId = trim(safeRequest.getEmployeeLoginId());
        if (isEmpty(employeeLoginId)) {
            return EmployeeValidationResult.invalid("ER001", List.of(ACCOUNT_NAME));
        }
        if (employeeLoginId.length() > 50) {
            return EmployeeValidationResult.invalid("ER006", List.of(ACCOUNT_NAME, "50"));
        }
        if (!ACCOUNT_NAME_PATTERN.matcher(employeeLoginId).matches()) {
            return EmployeeValidationResult.invalid("ER019", Collections.emptyList());
        }
        if (employeeRepository.findByEmployeeLoginId(employeeLoginId).isPresent()) {
            return EmployeeValidationResult.invalid("ER003", List.of(ACCOUNT_NAME));
        }

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

        String employeeLoginPassword = defaultString(safeRequest.getEmployeeLoginPassword());
        if (employeeLoginPassword.isEmpty()) {
            return EmployeeValidationResult.invalid("ER001", List.of(PASSWORD));
        }
        if (employeeLoginPassword.length() < 8 || employeeLoginPassword.length() > 50) {
            return EmployeeValidationResult.invalid("ER007", List.of(PASSWORD, "8", "50"));
        }

        String employeeLoginPasswordConfirm = defaultString(safeRequest.getEmployeeLoginPasswordConfirm());
        if (employeeLoginPasswordConfirm.isEmpty()) {
            return EmployeeValidationResult.invalid("ER001", List.of(PASSWORD_CONFIRM));
        }
        if (!employeeLoginPassword.equals(employeeLoginPasswordConfirm)) {
            return EmployeeValidationResult.invalid("ER017", Collections.emptyList());
        }

        String certificationId = trim(safeRequest.getCertificationId());
        if (isEmpty(certificationId)) {
            return EmployeeValidationResult.valid();
        }
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

    public static class EmployeeValidationResult {
        private final boolean valid;
        private final String errorCode;
        private final List<String> errorParams;

        private EmployeeValidationResult(boolean valid, String errorCode, List<String> errorParams) {
            this.valid = valid;
            this.errorCode = errorCode;
            this.errorParams = errorParams;
        }

        public static EmployeeValidationResult valid() {
            return new EmployeeValidationResult(true, null, Collections.emptyList());
        }

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
