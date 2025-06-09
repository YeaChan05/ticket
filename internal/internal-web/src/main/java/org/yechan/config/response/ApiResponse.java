package org.yechan.config.response;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApiResponse<T> {
    private String status;
    private T data;
    private LocalDateTime timestamp = LocalDateTime.now();


    public ApiResponse(String status, T data) {
        this.status = status;
        this.data = data;
    }
}
