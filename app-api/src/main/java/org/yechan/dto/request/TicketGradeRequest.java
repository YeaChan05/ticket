package org.yechan.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record TicketGradeRequest(
        @NotBlank(message = "티켓 등급은 필수 입력값입니다.")
        String grade,

        @NotNull(message = "티켓 가격은 필수 입력값입니다.")
        @Min(value = 0, message = "티켓 가격은 0 이상이어야 합니다.")
        BigDecimal price,

        @NotNull(message = "티켓 수량은 필수 입력값입니다.")
        @Min(value = 1, message = "티켓 수량은 1개 이상이어야 합니다.")
        Integer quantity
) {
}
