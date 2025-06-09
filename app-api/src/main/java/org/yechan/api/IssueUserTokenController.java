package org.yechan.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yechan.dto.request.IssueTokenRequest;
import org.yechan.dto.response.TokenHolder;
import org.yechan.error.UserErrorCode;
import org.yechan.error.exception.UserException;
import org.yechan.repository.UserRepository;

@RestController
@RequestMapping("/api/v1/auth")
public record IssueUserTokenController(UserRepository userRepository) {
    @PostMapping("/issueToken")
    public TokenHolder issueUserToken(IssueTokenRequest request) {
        if(!userRepository.existsByEmail(request.email())){
            throw new UserException("제공된 이메일을 가진 사용자는 존재하지 않습니다.", UserErrorCode.USER_NOT_FOUND);
        }
        return new TokenHolder("Token issued successfully");
    }
}
