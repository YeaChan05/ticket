package org.yechan.config.security;

import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.yechan.entity.User;

public class CustomerUserDetails extends AbstractUserDetails {
    public CustomerUserDetails(User user) {
        super(user.getEmail(), List.of(new SimpleGrantedAuthority(user.getRole().name())));
    }
}
