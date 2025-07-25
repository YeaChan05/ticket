package org.yechan.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.hibernate.validator.constraints.URL;
import org.yechan.entity.Show.Category;

public record ShowRegisterRequest(
        @NotBlank(message = "제목은 필수 입력값입니다.")
        String title,

        String description,

        @NotNull(message = "장르는 필수 입력값입니다.")
        Category category,

        @NotBlank(message = "썸네일 URL은 필수 입력값입니다.")
        @URL
        String thumbnailUrl,

        @NotNull(message = "티켓팅 시작일은 필수 입력값입니다.")
        LocalDateTime ticketingStartDate,

        @NotNull(message = "티켓팅 종료일은 필수 입력값입니다.")
        LocalDateTime ticketingEndDate,

        @NotNull(message = "공연 시작일은 필수 입력값입니다.")
        UUID hallId,

        @NotNull(message = "티켓 수량은 필수 입력값입니다.")
        @Min(value = 1, message = "티켓 수량은 1개 이상이어야 합니다.")
        Integer ticketCount,

        @NotEmpty(message = "공연 일정은 최소 하나 이상이어야 합니다.")
        List<ShowScheduleRegisterRequest> showScheduleRegisterRequests,

        @NotEmpty(message = "티켓 등급은 최소 하나 이상이어야 합니다.")
        List<TicketGradeRequest> ticketGradeRequests
) {
}
