package org.yechan.api;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yechan.dto.request.ShowRegisterRequest;
import org.yechan.dto.response.ShowRegisterResponse;

@RequestMapping("/api/v1/shows")
@RestController
public record ShowController() {

    @PostMapping
    public ShowRegisterResponse registerShow(@Valid @RequestBody ShowRegisterRequest request) {
        return new ShowRegisterResponse(null,null);
    }
}
