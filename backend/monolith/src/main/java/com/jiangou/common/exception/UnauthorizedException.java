package com.jiangou.common.exception;

import com.jiangou.common.constant.ErrorCodes;

public class UnauthorizedException extends BusinessException {

    public UnauthorizedException(String message) {
        super(ErrorCodes.UNAUTHORIZED, message);
    }
}
