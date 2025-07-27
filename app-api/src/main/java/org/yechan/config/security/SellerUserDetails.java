package org.yechan.config.security;

import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.yechan.entity.Seller;

public class SellerUserDetails extends AbstractUserDetails {
    public SellerUserDetails(Seller seller) {
        super(seller.getEmail(), List.of((GrantedAuthority) () -> "ROLE_SELLER"));
    }
}
