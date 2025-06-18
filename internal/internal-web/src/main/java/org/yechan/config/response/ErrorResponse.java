package org.yechan.config.response;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ErrorResponse {
    private String status;
    private String message;
    private LocalDateTime timestamp = LocalDateTime.now();

    public ErrorResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }
}
