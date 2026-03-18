package com.ermiyas.exchange.domain.repository.wallet;

import com.ermiyas.exchange.domain.model.Wallet;
import com.ermiyas.exchange.domain.repository.GenericRepository;

import java.util.Optional;

public interface WalletRepository extends GenericRepository<Wallet, Long> {
    Optional<Wallet> getByUserId(Long userId);
    Optional<Wallet> getByUserIdWithLock(Long userId);
    Long nextId();
}
