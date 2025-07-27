package org.yechan.service.validator;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.yechan.dto.request.ShowRegisterRequest;
import org.yechan.dto.request.TicketGradeRequest;
import org.yechan.entity.Hall;
import org.yechan.entity.Show;
import org.yechan.error.ShowErrorCode;
import org.yechan.error.exception.ShowException;
import org.yechan.repository.HallRepository;
import org.yechan.repository.ShowRepository;

/**
 * Validator class for Show-related operations.
 * This class encapsulates all validation logic for show registration.
 */
@Component
@RequiredArgsConstructor
public class ShowValidator {
    private final ShowRepository showRepository;
    private final HallRepository hallRepository;

    public void validateShowRegistration(ShowRegisterRequest request, Show show) {
        validateUniqueTitle(show.getTitle());
        validateTicketingDates(request);
        validateHallCapacity(request.hallId(), request.ticketCount());
        validateTicketCounts(request);
    }

    private void validateUniqueTitle(String title) {
        if (showRepository.existsByTitle(title)) {
            throw new ShowException("duplicate show title", ShowErrorCode.DUPLICATE_SHOW_TITLE);
        }
    }

    private void validateTicketingDates(ShowRegisterRequest request) {
        if (request.ticketingStartDate().isAfter(request.ticketingEndDate())) {
            throw new ShowException("ticketing start date cannot be after end date", ShowErrorCode.INVALID_TICKET_DATE);
        }
    }

    private void validateHallCapacity(UUID hallId, int ticketCount) {
        hallRepository.getHallByKey(hallId)
                .ifPresentOrElse(
                        hall -> validateHallCapacity(hall, ticketCount),
                        () -> {
                            throw new ShowException("hall not found", ShowErrorCode.HALL_NOT_FOUND);
                        }
                );
    }

    private void validateHallCapacity(Hall hall, int ticketCount) {
        if (hall.getCapacity() < ticketCount) {
            throw new ShowException("hall capacity is not enough",
                    ShowErrorCode.EXCEED_MAX_TICKET_COUNT);
        }
    }

    private void validateTicketCounts(ShowRegisterRequest request) {
        var summedTicketQuantities = request.ticketGradeRequests().stream()
                .mapToInt(TicketGradeRequest::quantity)
                .sum();
        
        if (summedTicketQuantities != request.ticketCount()) {
            throw new ShowException("ticket count does not match with ticket grades", 
                    ShowErrorCode.TICKET_COUNT_MISMATCH);
        }
    }
}
