package com.jiangou.common.exception;

import com.jiangou.common.constant.ErrorCodes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void invalidCodeMapsTo422() {
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY,
                handler.handleBusiness(new BusinessException(ErrorCodes.INVALID_CODE, "bad")).getStatusCode());
    }

    @Test
    void codeRateLimitMapsTo429() {
        assertEquals(HttpStatus.TOO_MANY_REQUESTS,
                handler.handleBusiness(new BusinessException(ErrorCodes.CODE_RATE_LIMIT, "limit")).getStatusCode());
    }

    @Test
    void unauthorizedMapsTo401() {
        assertEquals(HttpStatus.UNAUTHORIZED,
                handler.handleBusiness(new BusinessException(ErrorCodes.UNAUTHORIZED, "auth")).getStatusCode());
    }
}
