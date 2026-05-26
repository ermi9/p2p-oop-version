package com.ermiyas.exchange.domain.model.user;

import com.ermiyas.exchange.domain.model.Password;

public class AdminUser extends User {

    public AdminUser(Long id, String username, String email, Password password) {
        super(id, username, email, password);
    }

    @Override
    public String getRoleName() { return "ADMIN"; }
}
