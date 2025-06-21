package org.yechan.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public record ShowScheduleRegisterRequest(
        @NotBlank(message = "공연 시작일은 필수 입력값입니다.")
        LocalDateTime startDateTime,

        @NotBlank(message = "공연 종료일은 필수 입력값입니다.")
        LocalDateTime endDateTime
) {
}
