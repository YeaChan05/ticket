package org.yechan.service;

import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.yechan.error.BusinessException;

@Component
@RequiredArgsConstructor
public class DefaultPasswordVerifier implements PasswordVerifier {

    private final PasswordEncoder encoder;

    @Override
    public void verify(String raw,
                       String encoded,
                       Supplier<? extends BusinessException> onFail) {

        if (!encoder.matches(raw, encoded)) {
            throw onFail.get();
        }
    }
}
