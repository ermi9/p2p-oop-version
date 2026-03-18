package com.ermiyas.exchange.domain.vo;

import com.ermiyas.exchange.domain.exception.IllegalBetException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class Password {
    private final String hashedValue;

    private Password(String hashedValue) {
        this.hashedValue = hashedValue;
    }

    public boolean matches(String rawInput) {
        return this.hashedValue.equals(hash(rawInput));
    }

    public String masked() {
        return "********";
    }

    public static Password create(String rawInput) throws IllegalBetException {
        if (rawInput == null || rawInput.length() < 8) {
            throw new IllegalBetException("Password must be at least 8 characters");
        }
        return new Password(hash(rawInput));
    }

    private static String hash(String rawInput) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(rawInput.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
