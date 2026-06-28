package com.jiangou.common.exception;

import com.jiangou.common.constant.ErrorCodes;

public class NotFoundException extends BusinessException {

    public NotFoundException(String message) {
        super(ErrorCodes.NOT_FOUND, message);
    }
}
