package com.ermiyas.exchange.domain.model.user;

import com.ermiyas.exchange.domain.model.Wallet;
import com.ermiyas.exchange.domain.vo.Money;
import com.ermiyas.exchange.domain.vo.Password;

public class StandardUser extends User implements WalletOwner {
    private Wallet wallet;

    public StandardUser(Long id, String username, String email, Password password) {
        super(id, username, email, password);
    }

    @Override
    public Wallet getWallet() {
        return wallet;
    }

    @Override
    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    @Override
    public String getRoleName() {
        return "STANDARD_PLAYER";
    }

    @Override
    public void validateTransaction(Money amount) {
        // hook for future limits
    }
}
