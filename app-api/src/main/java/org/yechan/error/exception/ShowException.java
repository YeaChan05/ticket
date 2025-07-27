package org.yechan.error.exception;

import org.yechan.error.BusinessException;
import org.yechan.error.ErrorCode;

public class ShowException extends BusinessException {
    public ShowException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
