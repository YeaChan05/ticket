package org.yechan.service;

import java.util.function.Supplier;
import org.yechan.error.BusinessException;

@FunctionalInterface
public interface PasswordVerifier {
    void verify(String raw, String encoded, Supplier<? extends BusinessException> onFail);
}
