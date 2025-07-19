package org.yechan.service;

import java.time.LocalDateTime;
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


@Service
@RequiredArgsConstructor
public class ShowRegisterer implements ShowRegisterUseCase {
    private final ShowRepository showRepository;

    @Override
    @Transactional
    public ShowRegisterResponse register(ShowRegisterRequest request, Seller seller) {
        var uuid = request.hallId();
        Show show = ShowEntityConverter.CONVERTER.convert(request, seller, uuid);
        var showKey = showRepository.insert(show);
        var uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/shows/{key}")
                .buildAndExpand(showKey.toString())
                .toUriString();

        return new ShowRegisterResponse(LocalDateTime.now(), uri);
    }
}
