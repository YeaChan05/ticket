package org.yechan.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yechan.dto.response.TokenHolder;

@RestController
@RequestMapping("/api/v1/auth")
public record IssueUserTokenController() {
    @PostMapping("/issueToken")
    public TokenHolder issueUserToken() {
        return new TokenHolder("Token issued successfully");
    }
}
