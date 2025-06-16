package org.yechan;

import java.util.function.Consumer;

public class TestResult<S, E> {
    private final S success;
    private final E error;

    private TestResult(S success, E error) {
        this.success = success;
        this.error = error;
    }

    public static <S, E> TestResult<S, E> success(S success) {
        return new TestResult<>(success, null);
    }

    public static <S, E> TestResult<S, E> error(E error) {
        return new TestResult<>(null, error);
    }

    public boolean isSuccess() {
        return success != null;
    }

    public void onSuccess(Consumer<S> consumer) {
        if (isSuccess()) {
            consumer.accept(success);
        } else {
            throw new AssertionError("API call was expected to succeed, but it failed. Error: " + error.toString());
        }
    }

    public void onError(Consumer<E> consumer) {
        if (!isSuccess()) {
            consumer.accept(error);
        } else {
            throw new AssertionError("API call was expected to fail, but it succeeded. Success: " + success.toString());
        }
    }
}
