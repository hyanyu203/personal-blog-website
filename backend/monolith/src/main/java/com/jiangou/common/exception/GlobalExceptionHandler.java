package com.jiangou.common.exception;

import com.jiangou.common.constant.ErrorCodes;
import com.jiangou.common.result.ApiResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public org.springframework.http.ResponseEntity<ApiResult<Void>> handleBusiness(BusinessException e) {
        if (e.getCode() != ErrorCodes.RATE_LIMIT) {
            log.warn("Business error code={} message={}", e.getCode(), e.getMessage());
        }
        HttpStatus status = mapBusinessCodeToStatus(e.getCode());
        return org.springframework.http.ResponseEntity.status(status)
                .body(ApiResult.fail(e.getCode(), e.getMessage()));
    }

    private HttpStatus mapBusinessCodeToStatus(int code) {
        switch (code) {
            case ErrorCodes.UNAUTHORIZED:
                return HttpStatus.UNAUTHORIZED;
            case ErrorCodes.INVALID_CODE:
            case ErrorCodes.INVALID_CAPTCHA:
                return HttpStatus.UNPROCESSABLE_ENTITY;
            case ErrorCodes.CODE_RATE_LIMIT:
                return HttpStatus.TOO_MANY_REQUESTS;
            case ErrorCodes.FORBIDDEN:
                return HttpStatus.FORBIDDEN;
            case ErrorCodes.NOT_FOUND:
                return HttpStatus.NOT_FOUND;
            case ErrorCodes.CONFLICT:
                return HttpStatus.CONFLICT;
            case ErrorCodes.VALIDATION:
            case ErrorCodes.WEAK_PASSWORD:
                return HttpStatus.UNPROCESSABLE_ENTITY;
            case ErrorCodes.RATE_LIMIT:
                return HttpStatus.TOO_MANY_REQUESTS;
            default:
                return HttpStatus.BAD_REQUEST;
        }
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResult<Void> handleDataIntegrity(DataIntegrityViolationException e) {
        log.warn("Data integrity violation: {}", e.getMostSpecificCause().getMessage());
        return ApiResult.fail(ErrorCodes.CONFLICT, "数据冲突，请检查是否重复");
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<Void> handleValidation(Exception e) {
        String message = "参数校验失败";
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException) e;
            if (ex.getBindingResult().getFieldError() != null) {
                message = ex.getBindingResult().getFieldError().getDefaultMessage();
            }
        }
        return ApiResult.fail(ErrorCodes.VALIDATION, message);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResult<Void> handleUnknown(Exception e) {
        log.error("Unhandled exception", e);
        return ApiResult.fail(50000, "服务器内部错误");
    }
}
