package org.yechan.service;

import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.yechan.api.port.ShowRegisterUseCase;
import org.yechan.dto.ShowEntityConverter;
import org.yechan.dto.request.ShowRegisterRequest;
import org.yechan.dto.request.TicketGradeRequest;
import org.yechan.dto.response.ShowRegisterResponse;
import org.yechan.entity.Seller;
import org.yechan.entity.Show;
import org.yechan.error.ShowErrorCode;
import org.yechan.error.exception.ShowException;
import org.yechan.repository.HallRepository;
import org.yechan.repository.ShowRepository;


@Service
@RequiredArgsConstructor
public class ShowRegisterer implements ShowRegisterUseCase {
    private final ShowRepository showRepository;
    private final HallRepository hallRepository;

    @Override
    @Transactional
    public ShowRegisterResponse register(ShowRegisterRequest request, Seller seller) {
        var uuid = request.hallId();
        Show show = ShowEntityConverter.CONVERTER.convert(request, seller, uuid);

        if (showRepository.existByTitle(show.getTitle())) {
            throw new ShowException("duplicate show title", ShowErrorCode.DUPLICATE_SHOW_TITLE);
        }
        if (request.ticketingStartDate().isAfter(request.ticketingEndDate())) {
            throw new ShowException("ticketing start date cannot be after end date", ShowErrorCode.INVALID_TICKET_DATE);
        }

        hallRepository.getHallByKey(uuid)
                .ifPresentOrElse(
                        hall -> {
                            if (hall.getCapacity() < request.ticketCount()) {
                                throw new ShowException("hall capacity is not enough",
                                        ShowErrorCode.EXCEED_MAX_TICKET_COUNT);
                            }
                        },
                        () -> {
                            throw new ShowException("hall not found", ShowErrorCode.HALL_NOT_FOUND);
                        }
                );
        var summedTicketQuantities = request.ticketGradeRequests().stream().mapToInt(TicketGradeRequest::quantity).sum();
        if (summedTicketQuantities != request.ticketCount()) {
            throw new ShowException("ticket count does not match with ticket grades", ShowErrorCode.TICKET_COUNT_MISMATCH);
        }
        UUID showKey = showRepository.insert(show);

        var uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/shows/{key}")
                .buildAndExpand(requireNonNull(showKey).toString())
                .toUriString();

        return new ShowRegisterResponse(LocalDateTime.now(), uri);
    }
}
