package org.yechan.api;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sellers")
public record SellerController() {
    @PostMapping("/sign-up")
    public void registerSeller() {
    }
}
