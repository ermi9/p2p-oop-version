package com.ermiyas.exchange.domain.model.user;

import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.vo.Password;

public final class UserFactory {
    private UserFactory() { }

    public static StandardUser createStandard(Long id, String username, String email, String rawPassword) throws ExchangeException {
        return new StandardUser(id, username, email, Password.create(rawPassword));
    }

    public static AdminUser createAdmin(Long id, String username, String email, String rawPassword) throws ExchangeException {
        return new AdminUser(id, username, email, Password.create(rawPassword));
    }
}
