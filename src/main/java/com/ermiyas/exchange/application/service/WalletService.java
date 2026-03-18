package com.ermiyas.exchange.application.service;

import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.exception.UserNotFoundException;
import com.ermiyas.exchange.domain.model.Wallet;
import com.ermiyas.exchange.domain.model.user.User;
import com.ermiyas.exchange.domain.repository.user.UserRepository;
import com.ermiyas.exchange.domain.repository.wallet.WalletRepository;
import com.ermiyas.exchange.domain.vo.Money;

public class WalletService {
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    public WalletService(WalletRepository walletRepository, UserRepository userRepository) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
    }

    public void deposit(Long userId, Money amount) throws ExchangeException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found!"));
        Wallet wallet = user.getWallet();
        wallet.deposit(amount);
        walletRepository.save(wallet);
    }

    public void withdraw(Long userId, Money amount) throws ExchangeException {
        Wallet wallet = walletRepository.getByUserIdWithLock(userId).orElseThrow(() -> new UserNotFoundException("Wallet not found."));
        wallet.withdraw(amount);
        walletRepository.save(wallet);
    }

    public Wallet getUserWallet(Long userId) throws ExchangeException {
        return walletRepository.getByUserId(userId).orElseThrow(() -> new UserNotFoundException("Wallet not found."));
    }
}
