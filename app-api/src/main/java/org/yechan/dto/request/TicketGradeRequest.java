package org.yechan.dto.request;

import java.math.BigDecimal;

public record TicketGradeRequest(
        String grade,
        BigDecimal price,
        Integer quantity
) {
}
