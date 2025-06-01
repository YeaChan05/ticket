package org.yechan.config.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private final String status;
    private final T data;
    private final String message;
    private final LocalDateTime timestamp=LocalDateTime.now();

    public ApiResponse(String status, T data) {
        this.status = status;
        this.data = data;
        this.message = null;
    }

    public ApiResponse(String status, String message) {
        this.status = status;
        this.data = null;
        this.message = message;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("SUCCESS", data);
    }

    public static <T> ApiResponse<T> error(String status, String message) {
        return new ApiResponse<>(status, message);
    }
}
