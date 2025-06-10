package org.yechan.api.port;

import org.yechan.config.TokenHolder;
import org.yechan.dto.request.IssueTokenRequest;

public interface IssueTokenUseCase {
    TokenHolder issueToken(IssueTokenRequest request);
}
