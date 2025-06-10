package org.yechan.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yechan.api.port.IssueTokenUseCase;
import org.yechan.config.TokenHolder;
import org.yechan.dto.request.IssueTokenRequest;

@RestController
@RequestMapping("/api/v1/auth")
public record IssueUserTokenController(IssueTokenUseCase issueTokenUseCase) {
    @PostMapping("/issueToken")
    public TokenHolder issueUserToken(@RequestBody IssueTokenRequest request) {
        return issueTokenUseCase.issueToken(request);
    }
}
