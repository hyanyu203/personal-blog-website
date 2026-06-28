package com.jiangou.common.exception;

import com.jiangou.common.constant.ErrorCodes;

public class ForbiddenException extends BusinessException {

    public ForbiddenException(String message) {
        super(ErrorCodes.FORBIDDEN, message);
    }
}
