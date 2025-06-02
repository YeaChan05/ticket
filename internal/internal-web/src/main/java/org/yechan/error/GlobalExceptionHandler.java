package org.yechan.error;

import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@Component
@RestControllerAdvice(basePackages = "org.yechan.api")
//TODO 2025 06 01 15:27:37 : 애플리케이션 모듈 구조에 따라 basePackages를 변경해야 할 수도 있음
public class GlobalExceptionHandler {

    private static final String LOG_FORMAT = "Exception occurred: {} - Error Code: {}, Message: {}";

    @ExceptionHandler(BusinessException.class)
    protected ErrorCode handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        logException(e, errorCode);
        return errorCode;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ErrorCode handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        GlobalErrorCode errorCode = GlobalErrorCode.INVALID_REQUEST;
        String message = Optional.ofNullable(e.getBindingResult().getFieldError())
                .map(FieldError::getDefaultMessage)
                .orElse("잘못된 요청입니다.");
        errorCode.setMessage(message);
        logException(e, errorCode);
        return errorCode;
    }

    private void logException(final Exception e, final ErrorCode errorCode) {
        log.error(LOG_FORMAT,
                e.getClass(),
                errorCode.getCode(),
                errorCode.getMessage());
    }

    @Getter
    private enum GlobalErrorCode implements ErrorCode {
        INVALID_REQUEST("CONSTRAINT_VIOLATION");
        private final String code;
        @Setter
        private String message;

        GlobalErrorCode(final String code) {
            this.code = code;
        }
    }
}
