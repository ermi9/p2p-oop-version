package com.ermiyas.exchange.domain.model.user;

import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.exception.IllegalBetException;
import com.ermiyas.exchange.domain.model.Wallet;
import com.ermiyas.exchange.domain.vo.Password;

public class AdminUser extends User {
    public AdminUser(Long id, String username, String email, Password password) {
        super(id, username, email, password);
    }

    @Override
    public String getRoleName() {
        return "EXCHANGE_ADMIN";
    }

    @Override
    public Wallet getWallet() throws ExchangeException {
        throw new IllegalBetException("Admin users cannot own a wallet");
    }
}
