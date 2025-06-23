package org.yechan.service;

import static org.yechan.service.UserTokenIssuer.ClaimKey.EMAIL;
import static org.yechan.service.UserTokenIssuer.ClaimKey.ROLE;
import static org.yechan.service.UserTokenIssuer.ClaimKey.USERNAME;

import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yechan.api.port.IssueTokenUseCase;
import org.yechan.config.TokenProvider;
import org.yechan.dto.TokenHolder;
import org.yechan.dto.request.IssueTokenRequest;
import org.yechan.error.UserErrorCode;
import org.yechan.error.exception.UserException;
import org.yechan.repository.UserRepository;

@Service("userTokenIssuer")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserTokenIssuer implements IssueTokenUseCase {
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final PasswordVerifier passwordVerifier;

    @Override
    public TokenHolder issueToken(final IssueTokenRequest request) {
        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserException("사용자를 찾을 수 없습니다.", UserErrorCode.USER_NOT_FOUND));
        passwordVerifier.verify(
                request.password(), user.getPassword(),
                () -> new UserException("비밀번호가 일치하지 않습니다.", UserErrorCode.PASSWORD_MISMATCH)
        );
        var claims = Map.of(
                ROLE.getKey(), user.getRole().name(),
                EMAIL.getKey(), user.getEmail(),
                USERNAME.getKey(), user.getName()
        );
        return tokenProvider.createAccessToken(String.valueOf(user.getId()), claims);
    }

    @Getter
    enum ClaimKey {
        ROLE("role"),
        EMAIL("email"),
        USERNAME("username");

        private final String key;

        ClaimKey(String key) {
            this.key = key;
        }
    }
}
