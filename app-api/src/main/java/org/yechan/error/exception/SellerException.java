package org.yechan.error.exception;

import org.yechan.error.BusinessException;
import org.yechan.error.ErrorCode;

public class SellerException extends BusinessException {
    public SellerException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
