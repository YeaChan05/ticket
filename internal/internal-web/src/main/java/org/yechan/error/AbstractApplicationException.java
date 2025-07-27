package org.yechan.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public abstract class AbstractApplicationException extends RuntimeException {
    protected final String message;
    protected final ErrorCode errorCode;
}
