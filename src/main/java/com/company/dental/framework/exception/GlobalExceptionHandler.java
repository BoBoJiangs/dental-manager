package com.company.dental.framework.exception;

import com.company.dental.common.api.ApiResponse;
import com.company.dental.common.enums.ErrorCode;
import com.company.dental.common.exception.BusinessException;
import com.company.dental.framework.web.TraceContext;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleBusinessException(BusinessException ex) {
        log.warn("Business exception code={} message={} traceId={}", ex.getCode(), ex.getMessage(), MDC.get(TraceContext.TRACE_ID_KEY));
        return ApiResponse.fail(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleValidationException(Exception ex) {
        log.warn("Validation exception type={} traceId={}", ex.getClass().getSimpleName(), MDC.get(TraceContext.TRACE_ID_KEY));
        return ApiResponse.fail(ErrorCode.BAD_REQUEST.getCode(), ErrorCode.BAD_REQUEST.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleException(Exception ex) {
        log.error("Unhandled exception traceId={}", MDC.get(TraceContext.TRACE_ID_KEY), ex);
        return ApiResponse.fail(ErrorCode.SYSTEM_ERROR.getCode(), ErrorCode.SYSTEM_ERROR.getMessage());
    }
}
