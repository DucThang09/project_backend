/**
 * Copyright(C) 2026 Luvina Software Company
 *
 * EmployeeSearchValidator.java, 10/05/2026 tdthang
 */
package com.luvina.la.validator;

import static com.luvina.la.util.ValidationUtils.isEmpty;

import com.luvina.la.config.Constants;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Kiểm tra tính hợp lệ dữ liệu cho EmployeeSearchValidator.
 *
 * @author tdthang
 */

@Component
public class EmployeeSearchValidator {

    private static final String OFFSET_PARAM_NAME = "\u30aa\u30d5\u30bb\u30c3\u30c8";
    private static final String LIMIT_PARAM_NAME = "\u30ea\u30df\u30c3\u30c8";
    /**
     * Kiểm tra dữ liệu validate.
     *
     * @param ordEmployeeName tham số đầu vào của method
     * @param ordCertificationName tham số đầu vào của method
     * @param ordEndDate tham số đầu vào của method
     * @param offsetStr tham số đầu vào của method
     * @param limitStr tham số đầu vào của method
     * @return giá trị trả về sau khi xử lý
     */

    public EmployeeSearchValidationResult validate(
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

        return EmployeeSearchValidationResult.valid(offset, limit);
    }

    // Giá trị sort để trống được chấp nhận, chỉ báo lỗi khi khác ASC/DESC.
    /**
     * Kiểm tra dữ liệu isInvalidOrder.
     *
     * @param order tham số đầu vào của method
     * @return giá trị trả về sau khi xử lý
     */
    private boolean isInvalidOrder(String order) {
        if (isEmpty(order)) {
            return false;
        }

        return !"ASC".equals(order) && !"DESC".equals(order);
    }

    // Offset để trống thì dùng mặc định, hợp lệ khi là số nguyên >= 0.
    /**
     * Xử lý chức năng parseOffset.
     *
     * @param offsetStr tham số đầu vào của method
     * @return giá trị trả về sau khi xử lý
     */
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
    /**
     * Xử lý chức năng parseLimit.
     *
     * @param limitStr tham số đầu vào của method
     * @return giá trị trả về sau khi xử lý
     */
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

    // Gói kết quả validate để controller dùng lại thông tin phân trang đã kiểm tra.
    /**
     * Mô tả chức năng của EmployeeSearchValidationResult.
     *
     * @author tdthang
     */
    public static class EmployeeSearchValidationResult {

        private final boolean valid;
        private final String errorCode;
        private final List<String> errorParams;
        private final Integer offset;
        private final Integer limit;

        private EmployeeSearchValidationResult(
                boolean valid,
                String errorCode,
                List<String> errorParams,
                Integer offset,
                Integer limit
        ) {
            this.valid = valid;
            this.errorCode = errorCode;
            this.errorParams = errorParams;
            this.offset = offset;
            this.limit = limit;
        }

        // Trả về kết quả hợp lệ kèm dữ liệu phân trang đã kiểm tra.
        /**
         * Xử lý chức năng valid.
         *
         * @param offset tham số đầu vào của method
         * @param limit tham số đầu vào của method
         * @return giá trị trả về sau khi xử lý
         */
        public static EmployeeSearchValidationResult valid(
                Integer offset,
                Integer limit
        ) {
            return new EmployeeSearchValidationResult(
                    true,
                    null,
                    Collections.emptyList(),
                    offset,
                    limit
            );
        }

        // Trả về kết quả không hợp lệ kèm mã lỗi và tham số lỗi.
        /**
         * Xử lý chức năng invalid.
         *
         * @param errorCode tham số đầu vào của method
         * @param errorParams tham số đầu vào của method
         * @return giá trị trả về sau khi xử lý
         */
        public static EmployeeSearchValidationResult invalid(String errorCode, List<String> errorParams) {
            return new EmployeeSearchValidationResult(false, errorCode, errorParams, null, null);
        }
        /**
         * Kiểm tra dữ liệu isValid.
         *
         * @return giá trị trả về sau khi xử lý
         */

        public boolean isValid() {
            return valid;
        }
        /**
         * Lấy dữ liệu getErrorCode.
         *
         * @return giá trị trả về sau khi xử lý
         */

        public String getErrorCode() {
            return errorCode;
        }
        /**
         * Lấy dữ liệu getErrorParams.
         *
         * @return giá trị trả về sau khi xử lý
         */

        public List<String> getErrorParams() {
            return errorParams;
        }
        /**
         * Lấy dữ liệu getOffset.
         *
         * @return giá trị trả về sau khi xử lý
         */

        public Integer getOffset() {
            return offset;
        }
        /**
         * Lấy dữ liệu getLimit.
         *
         * @return giá trị trả về sau khi xử lý
         */

        public Integer getLimit() {
            return limit;
        }
    }
}
