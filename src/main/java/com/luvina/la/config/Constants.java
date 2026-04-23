package com.luvina.la.config;

import java.util.regex.Pattern;

public class Constants {

    private Constants() {
    }

    public static final String SPRING_PROFILE_DEVELOPMENT = "dev";
    public static final String SPRING_PROFILE_PRODUCTION = "prod";
    public static final boolean IS_CROSS_ALLOW = true;

    public static final String JWT_SECRET = "Luvina-Academe";
    public static final long JWT_EXPIRATION = 160 * 60 * 60; // 7 day
    public static final int DEFAULT_OFFSET = 0;
    public static final int DEFAULT_LIMIT = 20;

    public static final String ACCOUNT_NAME = "アカウント名";
    public static final String GROUP = "グループ";
    public static final String EMPLOYEE_NAME = "氏名";
    public static final String EMPLOYEE_NAME_KANA = "カタカナ氏名";
    public static final String BIRTH_DATE = "生年月日";
    public static final String EMAIL = "メールアドレス";
    public static final String TELEPHONE = "電話番号";
    public static final String PASSWORD = "パスワード";
    public static final String PASSWORD_CONFIRM = "パスワード（確認）";
    public static final String CERTIFICATION = "資格";
    public static final String CERTIFICATION_START_DATE = "資格交付日";
    public static final String CERTIFICATION_END_DATE = "失効日";
    public static final String SCORE = "点数";

    public static final Pattern ACCOUNT_NAME_PATTERN = Pattern.compile("^[A-Za-z_][A-Za-z0-9_]*$");
    public static final Pattern KATAKANA_PATTERN = Pattern.compile("^[\\u30a1-\\u30f6\\u30fc]+$");
    public static final Pattern HALF_WIDTH_PATTERN = Pattern.compile("^[\\x20-\\x7E]+$");
    public static final Pattern POSITIVE_INTEGER_PATTERN = Pattern.compile("^[1-9]\\d*$");
    public static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    // config endpoints public
    public static final String[] ENDPOINTS_PUBLIC = new String[] {
            "/",
            "/login/**",
            "/error/**"
    };

    // config endpoints for USER role
    public static final String[] ENDPOINTS_WITH_ROLE = new String[] {
            "/user/**",
            "/employee/**",
            "/certification/**"
    };

    // user attributies put to token
    public static final String[] ATTRIBUTIES_TO_TOKEN = new String[] {
            "employeeId",
            "employeeName",
            "employeeLoginId",
            "employeeEmail",
            "role"
    };
}
