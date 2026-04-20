package com.luvina.la.validator;

import com.luvina.la.config.Constants;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Xác thực và chuẩn hóa các tham số truy vấn của màn hình tìm kiếm nhân viên.
 */
@Component
public class EmployeeSearchValidator {

    private static final String OFFSET_PARAM_NAME = "オフセット";
    private static final String LIMIT_PARAM_NAME = "リミット";

    /**
     * Xác thực các tham số tìm kiếm danh sách nhân viên và trả về dữ liệu đã chuẩn hóa.
     *
     * @param employeeName tên nhân viên dùng để tìm kiếm
     * @param ordEmployeeName thứ tự sắp xếp theo tên nhân viên
     * @param ordCertificationName thứ tự sắp xếp theo tên chứng chỉ
     * @param ordEndDate thứ tự sắp xếp theo ngày hết hạn
     * @param offsetStr giá trị offset từ request
     * @param limitStr giá trị limit từ request
     * @return kết quả xác thực chứa dữ liệu đã chuẩn hóa hoặc thông tin lỗi
     */
    public EmployeeSearchValidationResult validate(
            String employeeName,
            String ordEmployeeName,
            String ordCertificationName,
            String ordEndDate,
            String offsetStr,
            String limitStr
    ) {
        if (isInvalidOrder(ordEmployeeName)
                || isInvalidOrder(ordCertificationName)
                || isInvalidOrder(ordEndDate)) {
            return EmployeeSearchValidationResult.invalid("ER021", Collections.emptyList());
        }

        Integer offset = parseOffset(offsetStr);
        if (offset == null) {
            return EmployeeSearchValidationResult.invalid("ER018", List.of(OFFSET_PARAM_NAME));
        }

        Integer limit = parseLimit(limitStr);
        if (limit == null) {
            return EmployeeSearchValidationResult.invalid("ER018", List.of(LIMIT_PARAM_NAME));
        }

        return EmployeeSearchValidationResult.valid(normalizeEmployeeName(employeeName), offset, limit);
    }

    /**
     * Kiểm tra giá trị sắp xếp có hợp lệ hay không.
     *
     * @param order chuỗi thứ tự sắp xếp
     * @return true nếu không hợp lệ
     */
    private boolean isInvalidOrder(String order) {
        if (isEmpty(order)) {
            return false;
        }

        return !"ASC".equals(order) && !"DESC".equals(order);
    }

    /**
     * Parse và xác thực giá trị offset.
     *
     * @param offsetStr giá trị offset thô
     * @return offset hợp lệ hoặc null nếu không hợp lệ
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

    /**
     * Parse và xác thực giá trị limit.
     *
     * @param limitStr giá trị limit thô
     * @return limit hợp lệ hoặc null nếu không hợp lệ
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

    /**
     * Kiểm tra chuỗi đầu vào có null hoặc rỗng sau khi trim hay không.
     *
     * @param value chuỗi đầu vào
     * @return true nếu rỗng
     */
    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Chuẩn hóa tên nhân viên bằng cách trim khoảng trắng và trả về null nếu rỗng.
     *
     * @param employeeName tên nhân viên đầu vào
     * @return tên nhân viên đã chuẩn hóa hoặc null
     */
    private String normalizeEmployeeName(String employeeName) {
        return isEmpty(employeeName) ? null : employeeName.trim();
    }

    /**
     * Chứa trạng thái xác thực và dữ liệu tìm kiếm nhân viên sau khi chuẩn hóa.
     */
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

        /**
         * Tạo kết quả hợp lệ với dữ liệu đã chuẩn hóa.
         *
         * @param normalizedEmployeeName tên nhân viên đã chuẩn hóa hoặc null
         * @param offset offset hợp lệ
         * @param limit limit hợp lệ
         * @return kết quả xác thực hợp lệ
         */
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

        /**
         * Tạo kết quả không hợp lệ kèm thông tin lỗi.
         *
         * @param errorCode mã lỗi trả về cho client
         * @param errorParams tham số đi kèm message lỗi
         * @return kết quả xác thực không hợp lệ
         */
        public static EmployeeSearchValidationResult invalid(String errorCode, List<String> errorParams) {
            return new EmployeeSearchValidationResult(false, errorCode, errorParams, null, null, null);
        }

        /**
         * Trả về trạng thái xác thực.
         *
         * @return true nếu hợp lệ
         */
        public boolean isValid() {
            return valid;
        }

        /**
         * Trả về mã lỗi khi xác thực thất bại.
         *
         * @return mã lỗi hoặc null
         */
        public String getErrorCode() {
            return errorCode;
        }

        /**
         * Trả về danh sách tham số lỗi khi xác thực thất bại.
         *
         * @return danh sách tham số lỗi
         */
        public List<String> getErrorParams() {
            return errorParams;
        }

        /**
         * Trả về tên nhân viên đã chuẩn hóa.
         *
         * @return tên nhân viên đã chuẩn hóa hoặc null
         */
        public String getNormalizedEmployeeName() {
            return normalizedEmployeeName;
        }

        /**
         * Trả về offset đã được xác thực.
         *
         * @return offset hoặc null nếu không hợp lệ
         */
        public Integer getOffset() {
            return offset;
        }

        /**
         * Trả về limit đã được xác thực.
         *
         * @return limit hoặc null nếu không hợp lệ
         */
        public Integer getLimit() {
            return limit;
        }
    }
}