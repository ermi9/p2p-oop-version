package com.ermiyas.exchange.domain.model;

import com.ermiyas.exchange.domain.exception.IllegalBetException;

public final class Password {

    private final String value;

    private Password(String value) {
        this.value = value;
    }

    public boolean matches(String rawInput) {
        return this.value.equals(rawInput);
    }

    public static Password create(String rawInput) throws IllegalBetException {
        if (rawInput == null || rawInput.length() < 6)
            throw new IllegalBetException("Password must be at least 6 characters");
        return new Password(rawInput);
    }
}
