package com.luvina.la.util;

import static com.luvina.la.config.Constants.ACCOUNT_NAME_PATTERN;
import static com.luvina.la.config.Constants.EMAIL_PATTERN;
import static com.luvina.la.config.Constants.KATAKANA_PATTERN;

import java.time.LocalDate;
import java.util.regex.Pattern;

public final class ValidationUtils {

    private static final String HALFSIZE_NUMBER_PATTERN = "^[0-9]*$";

    private ValidationUtils() {
    }

    /**
     * Kiểm tra chuỗi null, rỗng hoặc chỉ có khoảng trắng.
     *
     * @param value chuỗi cần kiểm tra
     * @return true nếu chuỗi rỗng, false nếu có dữ liệu
     */
    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static String escapeLikePattern(String value) {
        if (value == null) {
            return null;
        }

        String trimmedValue = value.trim();
        if (trimmedValue.isEmpty()) {
            return "";
        }

        return trimmedValue
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }

    /**
     * Kiểm tra độ dài tối đa của chuỗi.
     *
     * @param value chuỗi cần kiểm tra
     * @param maxLength độ dài tối đa
     * @return true nếu hợp lệ, false nếu không hợp lệ
     */
    public static boolean isValidMaxLength(String value, int maxLength) {
        if (value == null) {
            return true;
        }
        return value.length() <= maxLength;
    }

    /**
     * Kiểm tra độ dài tối thiểu của chuỗi.
     *
     * @param value chuỗi cần kiểm tra
     * @param minLength độ dài tối thiểu
     * @return true nếu hợp lệ, false nếu không hợp lệ
     */
    public static boolean isValidMinLength(String value, int minLength) {
        if (value == null) {
            return false;
        }
        return value.length() >= minLength;
    }

    /**
     * Kiểm tra định dạng Login ID.
     *
     * @param loginId Login ID cần kiểm tra
     * @return true nếu hợp lệ, false nếu không hợp lệ
     */
    public static boolean isValidLoginId(String loginId) {
        if (loginId == null || loginId.isEmpty()) {
            return true;
        }
        return ACCOUNT_NAME_PATTERN.matcher(loginId).matches();
    }

    /**
     * Kiểm tra định dạng email.
     *
     * @param email email cần kiểm tra
     * @return true nếu hợp lệ, false nếu không hợp lệ
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return true;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Kiểm tra định dạng Katakana.
     *
     * @param text chuỗi cần kiểm tra
     * @return true nếu hợp lệ, false nếu không hợp lệ
     */
    public static boolean isValidKatakana(String text) {
        if (text == null || text.isEmpty()) {
            return true;
        }
        return KATAKANA_PATTERN.matcher(text).matches();
    }

    /**
     * Kiểm tra định dạng số halfsize.
     *
     * @param text chuỗi cần kiểm tra
     * @return true nếu hợp lệ, false nếu không hợp lệ
     */
    public static boolean isHalfsizeNumber(String text) {
        if (text == null || text.isEmpty()) {
            return true;
        }
        return Pattern.matches(HALFSIZE_NUMBER_PATTERN, text);
    }

    /**
     * Kiểm tra định dạng ngày tháng ISO yyyy-MM-dd.
     *
     * @param date ngày tháng cần kiểm tra
     * @return true nếu hợp lệ, false nếu không hợp lệ
     */
    public static boolean isValidDateFormat(String date) {
        if (date == null || date.isEmpty()) {
            return true;
        }

        try {
            String[] parts = date.split("-");
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

            LocalDate.parse(date);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }
}
