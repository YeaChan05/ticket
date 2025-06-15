package org.yechan.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yechan.api.port.IssueTokenUseCase;
import org.yechan.config.TokenProvider;
import org.yechan.dto.TokenHolder;
import org.yechan.dto.request.IssueTokenRequest;
import org.yechan.error.UserErrorCode;
import org.yechan.error.exception.UserException;
import org.yechan.repository.UserRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TokenIssuer implements IssueTokenUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    @Override
    public TokenHolder issueToken(final IssueTokenRequest request) {
        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserException("사용자를 찾을 수 없습니다.", UserErrorCode.USER_NOT_FOUND));
        verifyPassword(user.getPassword(), request.password());
        var claims = Map.of(
                "role", user.getRole().name(),
                "email", user.getEmail(),
                "name", user.getName()
        );
        return tokenProvider.createAccessToken(String.valueOf(user.getId()), claims);
    }

    private void verifyPassword(final String password, final String requestedPassword) {
        if (!passwordEncoder.matches(requestedPassword, password)) {
            throw new UserException("비밀번호가 일치하지 않습니다.", UserErrorCode.PASSWORD_MISMATCH);
        }
    }
}
