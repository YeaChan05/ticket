package org.yechan;

import java.util.function.Consumer;
import org.yechan.config.response.ApiResponse;
import org.yechan.config.response.ErrorResponse;

public class TestResult<T> {
    private final ApiResponse<T> success;
    private final ErrorResponse error;

    private TestResult(ApiResponse<T> success, ErrorResponse error) {
        this.success = success;
        this.error = error;
    }

    public static<T> TestResult<T> success(ApiResponse<T> success) {
        return new TestResult<>(success, null);
    }

    public static <T> TestResult<T> error(ErrorResponse error) {
        return new TestResult<>(null, error);
    }

    public boolean isSuccess() {
        return success != null;
    }

    public TestResult<T> onSuccess(Consumer<ApiResponse<T>> consumer) {
        if (isSuccess()) {
            consumer.accept(success);
            return this;
        } else {
            throw new AssertionError("API call was expected to succeed, but it failed. Error: " + error.toString());
        }
    }

    public TestResult<T> onError(Consumer<ErrorResponse> consumer) {
        if (!isSuccess()) {
            consumer.accept(error);
            return this;
        } else {
            throw new AssertionError("API call was expected to fail, but it succeeded. Success: " + success.toString());
        }
    }
}
