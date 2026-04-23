package com.luvina.la.validator;
/**
 * Copyright(C) 2026 Luvina Software Company
 * <p>
 * EmployeeController.java, April 13, 2026 tdthang
 */
import com.luvina.la.config.Constants;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class EmployeeSearchValidator {

    private static final String OFFSET_PARAM_NAME = "\u30aa\u30d5\u30bb\u30c3\u30c8";
    private static final String LIMIT_PARAM_NAME = "\u30ea\u30df\u30c3\u30c8";

    public EmployeeSearchValidationResult validate(
            String employeeName,
            String ordEmployeeName,
            String ordCertificationName,
            String ordEndDate,
            String offsetStr,
            String limitStr
    ) {
        // Chỉ chấp nhận giá trị sort là ASC hoặc DESC.
        if (isInvalidOrder(ordEmployeeName)
                || isInvalidOrder(ordCertificationName)
                || isInvalidOrder(ordEndDate)) {
            return EmployeeSearchValidationResult.invalid("ER021", Collections.emptyList());
        }

        // Offset để trống thì lấy mặc định, sai định dạng thì trả lỗi.
        Integer offset = parseOffset(offsetStr);
        if (offset == null) {
            return EmployeeSearchValidationResult.invalid("ER018", List.of(OFFSET_PARAM_NAME));
        }

        // Limit để trống thì lấy mặc định, phải là số nguyên dương.
        Integer limit = parseLimit(limitStr);
        if (limit == null) {
            return EmployeeSearchValidationResult.invalid("ER018", List.of(LIMIT_PARAM_NAME));
        }

        // Chuẩn hóa tên nhân viên trước khi đưa xuống service/repository.
        return EmployeeSearchValidationResult.valid(normalizeEmployeeName(employeeName), offset, limit);
    }

    // Giá trị sort để trống được chấp nhận, chỉ báo lỗi khi khác ASC/DESC.
    private boolean isInvalidOrder(String order) {
        if (isEmpty(order)) {
            return false;
        }

        return !"ASC".equals(order) && !"DESC".equals(order);
    }

    // Offset để trống thì dùng mặc định, hợp lệ khi là số nguyên >= 0.
    private Integer parseOffset(String offsetStr) {
        if (isEmpty(offsetStr)) {
            return Constants.DEFAULT_OFFSET;
        }

        try {
            int value = Integer.parseInt(offsetStr);
            return value >= 0 ? value : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    // Limit để trống thì dùng mặc định, hợp lệ khi là số nguyên dương.
    private Integer parseLimit(String limitStr) {
        if (isEmpty(limitStr)) {
            return Constants.DEFAULT_LIMIT;
        }

        try {
            int value = Integer.parseInt(limitStr);
            return value > 0 ? value : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    // Dùng chung để kiểm tra chuỗi null, rỗng hoặc chỉ có khoảng trắng.
    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    // Tên tìm kiếm chỉ trim khoảng trắng, rỗng thì coi như không lọc.
    private String normalizeEmployeeName(String employeeName) {
        return isEmpty(employeeName) ? null : employeeName.trim();
    }

    // Gói kết quả validate để controller dùng lại thông tin đã chuẩn hóa.
    public static class EmployeeSearchValidationResult {

        private final boolean valid;
        private final String errorCode;
        private final List<String> errorParams;
        private final String normalizedEmployeeName;
        private final Integer offset;
        private final Integer limit;

        private EmployeeSearchValidationResult(
                boolean valid,
                String errorCode,
                List<String> errorParams,
                String normalizedEmployeeName,
                Integer offset,
                Integer limit
        ) {
            this.valid = valid;
            this.errorCode = errorCode;
            this.errorParams = errorParams;
            this.normalizedEmployeeName = normalizedEmployeeName;
            this.offset = offset;
            this.limit = limit;
        }

        // Trả về kết quả hợp lệ kèm dữ liệu đã chuẩn hóa.
        public static EmployeeSearchValidationResult valid(
                String normalizedEmployeeName,
                Integer offset,
                Integer limit
        ) {
            return new EmployeeSearchValidationResult(
                    true,
                    null,
                    Collections.emptyList(),
                    normalizedEmployeeName,
                    offset,
                    limit
            );
        }

        // Trả về kết quả không hợp lệ kèm mã lỗi và tham số lỗi.
        public static EmployeeSearchValidationResult invalid(String errorCode, List<String> errorParams) {
            return new EmployeeSearchValidationResult(false, errorCode, errorParams, null, null, null);
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

        public String getNormalizedEmployeeName() {
            return normalizedEmployeeName;
        }

        public Integer getOffset() {
            return offset;
        }

        public Integer getLimit() {
            return limit;
        }
    }
}
