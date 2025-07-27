package org.yechan.error;

public class BusinessException extends AbstractApplicationException {
    public BusinessException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
