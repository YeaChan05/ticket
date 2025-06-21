package org.yechan.dto.response;

import java.time.LocalDateTime;

public record ShowRegisterResponse(LocalDateTime applyTime,String redirectUrl) {
}
