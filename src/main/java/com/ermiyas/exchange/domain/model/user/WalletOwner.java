package com.ermiyas.exchange.domain.model.user;

import com.ermiyas.exchange.domain.model.Wallet;
import com.ermiyas.exchange.domain.vo.Money;

public interface WalletOwner {
    void setWallet(Wallet wallet);
    void validateTransaction(Money amount);
}
