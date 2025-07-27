package org.yechan.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record ShowScheduleRegisterRequest(
        @NotNull(message = "공연 시작일은 필수 입력값입니다.")
        LocalDateTime startDateTime,

        @NotNull(message = "공연 종료일은 필수 입력값입니다.")
        LocalDateTime endDateTime
) {
    @JsonIgnore
    @AssertTrue(message = "공연 시작일은 종료일보다 이전이어야 합니다.")
    public boolean isStartDateTimeBeforeEndDateTime() {
        return startDateTime != null && endDateTime != null
               && startDateTime.isBefore(endDateTime);
    }
}
