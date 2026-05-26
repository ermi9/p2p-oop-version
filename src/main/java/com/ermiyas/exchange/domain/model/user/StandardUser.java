package com.ermiyas.exchange.domain.model.user;

import com.ermiyas.exchange.domain.model.Password;
import com.ermiyas.exchange.domain.model.Wallet;

public class StandardUser extends User implements Tradeable {

    private Wallet wallet;

    public StandardUser(Long id, String username, String email, Password password) {
        super(id, username, email, password);
    }

    @Override
    public Wallet getWallet() { return wallet; }

    public void setWallet(Wallet wallet) { this.wallet = wallet; }

    @Override
    public String getRoleName() { return "PLAYER"; }
}
