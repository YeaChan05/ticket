package org.yechan.error;

import static lombok.AccessLevel.PRIVATE;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = PRIVATE)
public enum ShowErrorCode implements ErrorCode {
    DUPLICATE_SHOW_TITLE("SHOW-001", "이미 등록된 공연 제목입니다."),
    INVALID_TICKET_DATE("SHOW-002", "티켓 날짜가 유효하지 않습니다."),
    EXCEED_MAX_TICKET_COUNT("SHOW-003", "티켓 수량이 최대 좌석수를 초과합니다."),
    HALL_NOT_FOUND("SHOW-004", "존재하지 않는 공연장입니다."),
    TICKET_COUNT_MISMATCH("SHOW-005", "티켓 수량과 등급별 티켓 수량이 일치하지 않습니다.");
    private final String code;
    private final String message;
}
