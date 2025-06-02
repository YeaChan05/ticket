package org.yechan.error.exception;

import org.yechan.error.BusinessException;
import org.yechan.error.ErrorCode;

public class UserException extends BusinessException {
  public UserException(final String message, final ErrorCode errorCode) {
    super(message, errorCode);
  }
}
