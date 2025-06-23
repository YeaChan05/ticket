package org.yechan.api;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.yechan.dto.TokenHolder;
import org.yechan.dto.request.IssueTokenRequest;
import org.yechan.service.TokenIssuerType;

@RestController
@RequestMapping("/api/v1/auth")
public record IssueTokenController() {
    @PostMapping("/token")
    public TokenHolder issueToken(
            @Valid @RequestBody IssueTokenRequest request,
            @RequestParam TokenIssuerType issuerType
    ) {
        return issuerType.issue(request);
    }
}
