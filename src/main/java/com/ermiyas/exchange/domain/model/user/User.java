package com.ermiyas.exchange.domain.model.user;

import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.model.Wallet;
import com.ermiyas.exchange.domain.vo.Password;

public abstract class User implements UserInterface {
    private final Long id;
    private final String username;
    private final String email;
    private Password password;

    protected User(Long id, String username, String email, Password password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public Password getPassword() { return password; }

    @Override
    public boolean authenticate(String rawInput) {
        return this.password != null && this.password.matches(rawInput);
    }

    @Override
    public void updatePassword(Password newPassword) {
        this.password = newPassword;
    }

    public abstract String getRoleName();
    public abstract Wallet getWallet() throws ExchangeException;
}
