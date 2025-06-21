package org.yechan.service;


import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.yechan.api.port.IssueTokenUseCase;
import org.yechan.config.ApplicationContextProvider;
import org.yechan.dto.TokenHolder;
import org.yechan.dto.request.IssueTokenRequest;

@RequiredArgsConstructor
public enum TokenIssuerType {
    USER(UserTokenIssuer.class),
    SELLER(SellerTokenIssuer.class);

    private static final Map<TokenIssuerType, IssueTokenUseCase> ISSUER_HOLDER
            = new EnumMap<>(TokenIssuerType.class);

    static {
        Arrays.stream(values())
                .forEach(t ->
                        ISSUER_HOLDER.put(t,
                                ApplicationContextProvider.getBean(t.issuer)));
    }

    private final Class<? extends IssueTokenUseCase> issuer;

    public TokenHolder issue(IssueTokenRequest request) {
        return ISSUER_HOLDER.get(this).issueToken(request);
    }
}
