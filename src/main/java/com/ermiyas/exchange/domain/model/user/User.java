package com.ermiyas.exchange.domain.model.user;

import com.ermiyas.exchange.domain.model.Password;

public abstract class User {

    private final Long id;
    private final String username;
    private final String email;
    private Password password;

    protected User(Long id, String username, String email, Password password) {
        this.id       = id;
        this.username = username;
        this.email    = email;
        this.password = password;
    }

    public Long   getId()       { return id; }
    public String getUsername() { return username; }
    public String getEmail()    { return email; }

    public boolean authenticate(String rawInput) {
        return password != null && password.matches(rawInput);
    }

    public void updatePassword(Password newPassword) {
        this.password = newPassword;
    }

    public abstract String getRoleName();
}
