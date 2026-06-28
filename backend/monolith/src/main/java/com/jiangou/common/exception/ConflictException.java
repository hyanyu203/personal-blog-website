package com.jiangou.common.exception;

import com.jiangou.common.constant.ErrorCodes;

public class ConflictException extends BusinessException {

    public ConflictException(String message) {
        super(ErrorCodes.CONFLICT, message);
    }
}
