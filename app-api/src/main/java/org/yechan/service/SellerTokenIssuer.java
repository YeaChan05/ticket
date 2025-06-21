package org.yechan.service;

import static org.yechan.service.UserTokenIssuer.ClaimKey.EMAIL;
import static org.yechan.service.UserTokenIssuer.ClaimKey.ROLE;
import static org.yechan.service.UserTokenIssuer.ClaimKey.USERNAME;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yechan.api.port.IssueTokenUseCase;
import org.yechan.config.TokenProvider;
import org.yechan.dto.TokenHolder;
import org.yechan.dto.request.IssueTokenRequest;
import org.yechan.error.SellerErrorCode;
import org.yechan.error.exception.SellerException;
import org.yechan.repository.SellerRepository;

@Service("sellerTokenIssuer")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SellerTokenIssuer implements IssueTokenUseCase {
    private final SellerRepository sellerRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    @Override
    public TokenHolder issueToken(final IssueTokenRequest request) {
        var seller = sellerRepository.findByEmail(request.email())
                .orElseThrow(() -> new SellerException("판매자를 찾을 수 없습니다.", SellerErrorCode.SELLER_NOT_FOUND));
        verifyPassword(seller.getPassword(), request.password());
        var claims = Map.of(
                ROLE.getKey(), "SELLER",
                EMAIL.getKey(), seller.getEmail(),
                USERNAME.getKey(), seller.getName()
        );
        return tokenProvider.createAccessToken(String.valueOf(seller.getId()), claims);
    }

    private void verifyPassword(final String password, final String requestedPassword) {
        if (!passwordEncoder.matches(requestedPassword, password)) {
            throw new SellerException("비밀번호가 일치하지 않습니다.", SellerErrorCode.PASSWORD_MISMATCH);
        }
    }
}
