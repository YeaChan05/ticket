package org.yechan.api;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yechan.api.port.IssueTokenUseCase;
import org.yechan.dto.TokenHolder;
import org.yechan.dto.request.IssueTokenRequest;

@RestController
@RequestMapping("/api/v1/auth")
public record IssueUserTokenController(
        IssueTokenUseCase issueTokenUseCase
) {
    @PostMapping("/token")
    public TokenHolder issueUserToken(@Valid @RequestBody IssueTokenRequest request) {
        return issueTokenUseCase.issueToken(request);
    }
}
