package org.yechan.service;

import static org.yechan.config.security.ClaimKey.EMAIL;
import static org.yechan.config.security.ClaimKey.ROLE;
import static org.yechan.config.security.ClaimKey.USERNAME;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yechan.api.port.IssueTokenUseCase;
import org.yechan.config.security.TokenProvider;
import org.yechan.dto.TokenHolder;
import org.yechan.dto.request.IssueTokenRequest;
import org.yechan.error.SellerErrorCode;
import org.yechan.error.exception.SellerException;
import org.yechan.repository.SellerRepository;

@Service("sellerTokenIssuer")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SellerTokenIssuer implements IssueTokenUseCase {
    private static final String SELLER = "SELLER";
    private final SellerRepository sellerRepository;
    private final TokenProvider tokenProvider;
    private final PasswordVerifier passwordVerifier;

    @Override
    public TokenHolder issueToken(final IssueTokenRequest request) {
        var seller = sellerRepository.findByEmail(request.email())
                .orElseThrow(() -> new SellerException("판매자를 찾을 수 없습니다.", SellerErrorCode.SELLER_NOT_FOUND));

        passwordVerifier.verify(
                request.password(), seller.getPassword(),
                () -> new SellerException("비밀번호가 일치하지 않습니다.", SellerErrorCode.PASSWORD_MISMATCH)
        );

        var claims = Map.of(
                ROLE.getKey(), SELLER,
                EMAIL.getKey(), seller.getEmail(),
                USERNAME.getKey(), seller.getName()
        );
        return tokenProvider.createAccessToken(String.valueOf(seller.getId()), claims);
    }

}
