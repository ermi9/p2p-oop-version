package com.ermiyas.exchange.domain.model.user;

import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.model.Wallet;
import com.ermiyas.exchange.domain.vo.Password;

public interface UserInterface {
    boolean authenticate(String rawInput);
    void updatePassword(Password newPassword);
    String getRoleName();
    Wallet getWallet() throws ExchangeException;
}
