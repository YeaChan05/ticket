package org.yechan.error;

public class GlobalException extends AbstractApplicationException {
    public GlobalException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
