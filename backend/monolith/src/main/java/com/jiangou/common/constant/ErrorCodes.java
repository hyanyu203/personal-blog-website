package com.jiangou.common.constant;

public final class ErrorCodes {

    public static final int UNAUTHORIZED = 40101;
    public static final int INVALID_CODE = 40102;
    public static final int INVALID_CAPTCHA = 40103;
    public static final int CODE_RATE_LIMIT = 40104;
    public static final int FORBIDDEN = 40301;
    public static final int CSRF = 40302;
    public static final int NOT_FOUND = 40401;
    public static final int CONFLICT = 40901;
    public static final int VALIDATION = 42201;
    public static final int WEAK_PASSWORD = 42202;
    public static final int RATE_LIMIT = 42901;

    private ErrorCodes() {
    }
}
