package org.yechan.config.response;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ErrorResponse {
    private final String status;
    private final String message;
    private final LocalDateTime timestamp = LocalDateTime.now();

    public ErrorResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }
}
