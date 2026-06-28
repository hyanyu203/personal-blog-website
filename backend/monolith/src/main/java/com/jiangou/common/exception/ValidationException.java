package com.jiangou.common.exception;

import com.jiangou.common.constant.ErrorCodes;

public class ValidationException extends BusinessException {

    public ValidationException(String message) {
        super(ErrorCodes.VALIDATION, message);
    }
}
