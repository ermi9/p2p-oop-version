package com.ermiyas.exchange.domain.repository.user;

import com.ermiyas.exchange.domain.model.user.User;
import com.ermiyas.exchange.domain.repository.GenericRepository;

import java.util.Optional;

public interface UserRepository extends GenericRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Long nextId();
}
