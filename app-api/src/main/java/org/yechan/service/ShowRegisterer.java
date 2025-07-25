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
import org.yechan.dto.response.ShowRegisterResponse;
import org.yechan.entity.Seller;
import org.yechan.entity.Show;
import org.yechan.repository.ShowRepository;
import org.yechan.service.validator.ShowValidator;


@Service
@RequiredArgsConstructor
public class ShowRegisterer implements ShowRegisterUseCase {
    private final ShowRepository showRepository;
    private final ShowValidator showValidator;

    @Override
    @Transactional
    public ShowRegisterResponse register(ShowRegisterRequest request, Seller seller) {
        Show show = convertToEntity(request, seller);
        showValidator.validateShowRegistration(request, show);

        UUID showKey = persistShow(show);
        return createResponse(showKey);
    }

    private Show convertToEntity(ShowRegisterRequest request, Seller seller) {
        var hallId = request.hallId();
        return ShowEntityConverter.CONVERTER.convert(request, seller, hallId);
    }
    

    private UUID persistShow(Show show) {
        return showRepository.insert(show);
    }

    private ShowRegisterResponse createResponse(UUID showKey) {
        var uri = buildRedirectUri(showKey);
        return new ShowRegisterResponse(LocalDateTime.now(), uri);
    }

    private String buildRedirectUri(UUID showKey) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/shows/{key}")
                .buildAndExpand(requireNonNull(showKey).toString())
                .toUriString();
    }
}
