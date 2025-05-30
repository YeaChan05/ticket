package org.yechan.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepository{
    private final JpaUserRepository jpaUserRepository;
}
